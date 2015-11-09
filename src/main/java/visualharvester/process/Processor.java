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

	public Processor(TweetSource source) {
		this.source = source;
	}

	public List<Tweet> augmentTweets(String criteria) {
		log.debug("Augmenting Tweets with criteria: " + criteria + " " + source.getClass().getName());

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
				log.debug("Location found in metadata");
				geoTweets++;
				final Location location = new Location(statusLocation.getLatitude(), statusLocation.getLongitude());
				tweet.setLocation(location);
			} else {
				// TODO Entity Extraction via OpenNLP or Clavin
			}

			if (tweet.getLocation() != null) {
				final List<String> images = tweet.getImageUrls();

				// Extract URL from Tweet Text
				final String tweetUrl = new UrlExtractor().extractUrl(tweet.getText());
				if ((tweetUrl != null) && !tweetUrl.isEmpty()) {
					tweet.setTweetUrl(tweetUrl);
					log.debug("Tweet contains URL: " + tweetUrl);
				}

				// Extract Images from Tweet's URL
				if (tweet.getTweetUrl() != null) {
					final List<String> tweetImageUrls = new ImageExtractor().extractImageUrls(tweet.getTweetUrl());
					log.debug("Found " + tweetImageUrls.size() + " images within the Tweet's URL");
					images.addAll(tweetImageUrls);
				}

				// Find nearby articles and extract images from them
				if (tweet.getLocation().isInitialized()) {
					final List<String> articleList = new NearbyArticleExtractor()
							.getNearbyArticles(tweet.getLocation().getLatitude(), tweet.getLocation().getLongitude());

					log.debug("Found " + articleList.size() + " nearby articles");
					for (final String string : articleList) {
						final String articleUrl = wikibase + string.replace(" ", "_");
						final List<String> wikipediaImageUrls = new ImageExtractor().extractImageUrls(articleUrl);
						images.addAll(wikipediaImageUrls);
					}

				}

				log.debug("Total Images associated with this tweet: " + images.size());
			}

		}
		log.debug("Found a total of " + geoTweets + " geotagged tweets");

		return augmentedTweets;
	}

}
