package visualharvester.sources;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

/**
 * Stream Listener class to support the StreamTweetSource class
 */
public class StreamListener implements StatusListener
{
   /** The Logger */
   Logger log = Logger.getLogger(getClass());
   /** List of Status objects */
   private List<Status> tweets;
   /** String for query criteria */
   private final String queryString;
   /** flag for allowing retweets */
   private final boolean allowRetweets;

   /**
    * Constructor
    *
    * @param queryString
    *           String for criteria
    * @param allowRetweets
    *           boolean for allowing of retweets
    */
   public StreamListener(final String queryString, final boolean allowRetweets)
   {
      this.queryString = queryString;
      this.allowRetweets = allowRetweets;
   }

   /**
    * Method to return the List of obtained Tweets
    *
    * @return List<Status>
    */
   public List<Status> getTweets()
   {
      if (tweets == null)
      {
         tweets = new ArrayList<>();
      }
      return tweets;
   }

   @Override
   public void onDeletionNotice(final StatusDeletionNotice statusDeletionNotice)
   {
      // Unused for application purposes; included to meet interface contract
   }

   @Override
   public void onException(final Exception ex)
   {
      // Unused for application purposes; included to meet interface contract
   }

   @Override
   public void onScrubGeo(final long userId, final long upToStatusId)
   {
      // Unused for application purposes; included to meet interface contract
   }

   @Override
   public void onStallWarning(final StallWarning warning)
   {
      // Unused for application purposes; included to meet interface contract
   }

   @Override
   public void onStatus(final Status status)
   {
      log.debug("Status Encountered");

      if (status.isRetweet() && !allowRetweets)
      {
         return;
      }

      if (queryString == null || queryString.isEmpty())
      {
         getTweets().add(status);
      }
      else
      {
         if (status.getText().contains(queryString))
         {
            getTweets().add(status);
         }
      }

      log.debug("Encountered " + getTweets().size() + " tweets");
   }

   @Override
   public void onTrackLimitationNotice(final int numberOfLimitedStatuses)
   {
      // Unused for application purposes; included to meet interface contract
   }
}
