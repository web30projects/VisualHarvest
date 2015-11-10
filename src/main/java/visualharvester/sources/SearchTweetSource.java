package visualharvester.sources;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class SearchTweetSource implements TweetSource {

	Logger log = Logger.getLogger(getClass());

	private final Twitter twitter;
	private int limit = 50;
	private boolean allowRetweets = true;

	public SearchTweetSource(Twitter twitter) {
		this.twitter = twitter;
	}

	@Override
	public void disableRetweets() {
		allowRetweets = false;
	}

	@Override
	public List<Status> getTweets(String containsText) {
		log.debug("Obtaining " + limit + " tweets via Twitter Search API");

		final Query query;

		if (allowRetweets) {
			query = new Query(containsText);
		} else {
			query = new Query(containsText + " -RT");
		}

		query.setLang("en");
		query.setCount(limit);
		try {
			final QueryResult queryResult = twitter.search(query);
			return queryResult.getTweets();
		} catch (final TwitterException e) {
			log.error("Error retrieving tweets from Twitter Search API", e);
		}
		return new ArrayList<>();
	}

	@Override
	public void sourceLimit(int limit) {
		this.limit = limit;
	}

}
