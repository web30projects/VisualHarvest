package visualharvester.sources;

import java.util.List;

import twitter4j.Status;

public interface TweetSource {

	public void disableRetweets();

	List<Status> getTweets(String containsText);

	public void sourceLimit(int limit);
}
