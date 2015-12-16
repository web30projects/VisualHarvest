package visualharvester.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.GeoLocation;
import twitter4j.Status;
import visualharvester.extractors.EntityExtractor;
import visualharvester.extractors.ImageExtractor;
import visualharvester.extractors.NearbyArticleExtractor;
import visualharvester.extractors.UrlExtractor;
import visualharvester.objects.Location;
import visualharvester.objects.Tweet;
import visualharvester.sources.TweetSource;
import visualharvester.storage.Storage;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.GeoParserFactory;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * The Core Processing class
 */
public class Processor
{

   /** The Logger */
   Logger log = Logger.getLogger(getClass());

   /** Tweet Source reference */
   TweetSource source;

   /** Wikipedia's Base URL */
   String wikibase = "https://en.wikipedia.org/wiki/";

   // TODO Extract these into better properties
   /** Local directory to store tweet images */
   String localPath = "C:/tweets";
   /** Local directory where clavin's Index directory is located */
   String indexPath = "C:/clavin/IndexDirectory";

   /** Claving GeoParser object */
   GeoParser geoparser = null;
   /** The Tweet Store reference */
   Storage store = null;

   /**
    * Constructor
    *
    * @param source
    *           Accepts the TweetSource implementor being used
    */
   public Processor(final TweetSource source)
   {
      this.source = source;
      try
      {
         geoparser = GeoParserFactory.getDefault(indexPath);
      }
      catch (final ClavinException e)
      {
         log.error("Error initiating Clavin Geoparser", e);
      }
   }

   /**
    * Method to use the configured TweetSource and augment tweets obtained from that source with geospatial/visual
    * information
    *
    * @param criteria
    *           String containing the search criteria / query terms
    * @param ignoreCoordinates
    *           boolean if tweets should continue to be augmented if no geospatial data can be found
    * @return List<Tweet>
    */
   public List<Tweet> augmentTweets(final String criteria, final boolean ignoreCoordinates)
   {
      final List<Tweet> augmentedTweets = new ArrayList<>();

      final List<Status> tweetList = source.getTweets(criteria);
      log.debug("found " + tweetList.size() + " tweets");

      int geoTweets = 0;
      for (final Status status : tweetList)
      {
         final Tweet tweet = new Tweet();
         tweet.setId(String.valueOf(status.getId()));
         log.debug("id: " + tweet.getId());
         tweet.setText(status.getText());

         // Handle location details
         final GeoLocation statusLocation = status.getGeoLocation();
         if (statusLocation != null)
         {
            geoTweets++;
            final Location location = new Location(statusLocation.getLatitude(), statusLocation.getLongitude());
            tweet.setLocation(location);
         }
         else
         {
            if (geoparser != null)
            {
               final EntityExtractor entityExtractor = new EntityExtractor(geoparser);
               final List<ResolvedLocation> extractEntities = entityExtractor.extractEntities(status.getText());

               for (final ResolvedLocation resolvedLocation : extractEntities)
               {
                  tweet.getExtractedEntities()
                        .add(resolvedLocation.getMatchedName() + " with a confidence of "
                              + resolvedLocation.getConfidence());
               }

               if (extractEntities.size() == 1)
               {
                  geoTweets++;
                  final Location location = new Location(extractEntities.get(0).getGeoname().getLatitude(),
                        extractEntities.get(0).getGeoname().getLongitude());
                  tweet.setLocation(location);
               }
               else if (extractEntities.size() > 1)
               {

                  log.debug("Found multiple entities (" + extractEntities.size() + ")");
                  /**
                   * TODO: compare nearness of entities, if close ->select location with greatest populates, if not
                   * close, tweet has entity ambiguity and we cannot make location assumptions
                   */
               }
            }

         }

         final List<String> images = tweet.getImageUrls();

         if (tweet.getLocation().isInitialized() || ignoreCoordinates)
         {
            // Extract URL from Tweet Text
            final String tweetUrl = new UrlExtractor().extractUrl(tweet.getText());
            if (tweetUrl != null && !tweetUrl.isEmpty())
            {
               tweet.setTweetUrl(tweetUrl);
            }

            // Extract Images from Tweet's URL
            if (tweet.getTweetUrl() != null)
            {
               images.addAll(processUrlForImages(tweet.getTweetUrl()));
            }

            // Find nearby articles and extract images from them
            if (tweet.getLocation().isInitialized())
            {
               final List<String> articleList = new NearbyArticleExtractor().getNearbyArticles(tweet);

               for (final String string : articleList)
               {
                  final String articleUrl = wikibase + string.replace(" ", "_");
                  images.addAll(processUrlForImages(articleUrl));
               }
            }
         }

         if (store != null)
         {
            store.insertTweet(tweet, criteria);
         }
         augmentedTweets.add(tweet);
      }

      log.debug("Found a total of " + geoTweets + " geotagged tweets");
      log.debug("Processing completed for query '" + criteria + "'");
      return augmentedTweets;
   }

   /**
    * Local Image Storage Path Getter
    *
    * @return String
    */
   public String getLocalPath()
   {
      return localPath;
   }

   /**
    * Tweet Storage Getter
    *
    * @return Storage implementor
    */
   public Storage getStore()
   {
      return store;
   }

   /**
    * Method to process Tweet URLs for Image URLs
    *
    * @param url
    *           String containing a Tweet URL
    * @return List<String> List of Image URLs obtained from ImageExtractor
    */
   private List<String> processUrlForImages(final String url)
   {
      return new ImageExtractor(localPath).extractImageUrls(url);
   }

   /**
    * Local Image Storage Path Setter
    *
    * @param localPath
    *           String
    */
   public void setLocalPath(final String localPath)
   {
      this.localPath = localPath;
   }

   /**
    * Tweet Storage Setter
    *
    * @param store
    *           Storage implementor
    */
   public void setStore(final Storage store)
   {
      this.store = store;
   }
}
