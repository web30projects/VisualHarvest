package visualharvester.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.GeoParserFactory;
import com.bericotech.clavin.resolver.ResolvedLocation;

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

public class Processor {

	Logger log = Logger.getLogger(getClass());

	TweetSource source;
	String wikibase = "https://en.wikipedia.org/wiki/";

	// TODO Extract these into better properties
	String localPath = "C:/Users/michael/Desktop/tweets";
	String indexPath = "C:/clavin/CLAVIN/IndexDirectory";
	GeoParser geoparser = null;
	Storage store = null;

	public Processor(TweetSource source) {
		this.source = source;
		try {
			geoparser = GeoParserFactory.getDefault(indexPath);
		} catch (final ClavinException e) {
			log.error("Error initiating Clavin Geoparser", e);
		}
	}

	public List<Tweet> augmentTweets(String criteria, boolean ignoreCoordinates) {
		final List<Tweet> augmentedTweets = new ArrayList<>();

		final List<Status> tweetList = source.getTweets(criteria);
		log.debug("found " + tweetList.size() + " tweets");

		int geoTweets = 0;
		for (final Status status : tweetList) {
			final Tweet tweet = new Tweet();
			tweet.setId(status.getId());
			tweet.setText(status.getText());

			// Handle location details
			final GeoLocation statusLocation = status.getGeoLocation();
			if (statusLocation != null) {
				geoTweets++;
				final Location location = new Location(statusLocation.getLatitude(), statusLocation.getLongitude());
				tweet.setLocation(location);
			} else {
				if (geoparser != null) {
					final EntityExtractor entityExtractor = new EntityExtractor(geoparser);
					final List<ResolvedLocation> extractEntities = entityExtractor.extractEntities(status.getText());

					for (final ResolvedLocation resolvedLocation : extractEntities) {
						tweet.getExtractedEntities().add(resolvedLocation.getMatchedName());
					}

					if (extractEntities.size() == 1) {
						geoTweets++;
						final Location location = new Location(extractEntities.get(0).getGeoname().getLatitude(),
								extractEntities.get(0).getGeoname().getLongitude());
						tweet.setLocation(location);
					} else if (extractEntities.size() > 1) {

						// log.debug("Found multiple entities (" +
						// extractEntities.size() + ")");
						// TODO: compare nearness of entities, if close ->select
						// location with greatest populates, if not close, tweet
						// has entity ambiguity and we cannot make location
						// assumptions
					}
				}

			}

			final List<String> images = tweet.getImageUrls();

			if (tweet.getLocation().isInitialized() || ignoreCoordinates) {
				// Extract URL from Tweet Text
				final String tweetUrl = new UrlExtractor().extractUrl(tweet.getText());
				if ((tweetUrl != null) && !tweetUrl.isEmpty()) {
					tweet.setTweetUrl(tweetUrl);
				}

				// Extract Images from Tweet's URL
				if (tweet.getTweetUrl() != null) {
					images.addAll(processUrlForImages(tweet.getTweetUrl(), tweet.getId().toString()));
				}

				// Find nearby articles and extract images from them
				if (tweet.getLocation().isInitialized()) {
					final List<String> articleList = new NearbyArticleExtractor().getNearbyArticles(tweet);

					for (final String string : articleList) {
						final String articleUrl = wikibase + string.replace(" ", "_");
						images.addAll(processUrlForImages(articleUrl, string));
					}
				}
			}

			augmentedTweets.add(tweet);
		}

		log.debug("Found a total of " + geoTweets + " geotagged tweets");

		if (store != null) {
			store.clearTweets(criteria);
			store.storeTweets(augmentedTweets, criteria);
		}

		return augmentedTweets;
	}

	public String getLocalPath() {
		return localPath;
	}

	public Storage getStore() {
		return store;
	}

	private List<String> processUrlForImages(String url, String identifier) {
		// log.debug("Extracting images for Tweet: " + identifier);
		return new ImageExtractor(localPath).extractImageUrls(url);
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public void setStore(Storage store) {
		this.store = store;
	}
}
