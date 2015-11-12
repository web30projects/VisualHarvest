package visualharvester.process;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.GeoLocation;
import twitter4j.Status;
import visualharvester.extractors.ImageExtractor;
import visualharvester.extractors.NearbyArticleExtractor;
import visualharvester.extractors.UrlExtractor;
import visualharvester.objects.Location;
import visualharvester.objects.Tweet;
import visualharvester.sources.TweetSource;

public class Processor {

	Logger log = Logger.getLogger(getClass());

	TweetSource source;
	String wikibase = "https://en.wikipedia.org/wiki/";
	String localPath = "C:\\Users\\Michael\\Desktop\\tweets";

	public Processor(TweetSource source) {
		this.source = source;
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
				// TODO Entity Extraction via OpenNLP or Clavin
			}

			final List<String> images = tweet.getImageUrls();

			if ((tweet.getLocation().isInitialized() == true) || ignoreCoordinates) {
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
		}

		log.debug("Found a total of " + geoTweets + " geotagged tweets");

		// TODO Place results into mongodb

		return augmentedTweets;
	}

	public String getLocalPath() {
		return localPath;
	}

	private List<String> processUrlForImages(String url, String identifier) {
		log.debug("Extracting images for Tweet: " + identifier);
		return new ImageExtractor(localPath).extractImageUrls(url);
	}

	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
}
