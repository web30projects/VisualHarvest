package visualharvester.sources;

import java.util.List;

import twitter4j.Status;

public interface TweetSource {

	List<Status> getTweets(String containsText);

	public void maxResults(int limit);
}
