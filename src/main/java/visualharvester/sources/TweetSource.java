package visualharvester.sources;

import java.util.List;

import twitter4j.Status;

public interface TweetSource {

	List<Status> getTweets(String criteria);
}
