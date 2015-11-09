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

	public SearchTweetSource(Twitter twitter) {
		this.twitter = twitter;
	}

	@Override
	public List<Status> getTweets(String queryString) {
		log.debug("Obtaining tweets via Twitter Search API");
		final Query query = new Query(queryString + " -RT");
		query.setLang("en");
		query.setCount(100);
		try {
			final QueryResult queryResult = twitter.search(query);
			return queryResult.getTweets();
		} catch (final TwitterException e) {
			log.error("Error retrieving tweets from Twitter Search API", e);
		}
		return new ArrayList<>();
	}

}
