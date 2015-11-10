package visualharvester.sources;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterStream;

public class StreamTweetSource implements TweetSource {

	private int minutes = 1;
	private int limit = 50;
	private final TwitterStream stream;

	public StreamTweetSource(TwitterStream stream) {
		this.stream = stream;
	}

	@Override
	public List<Status> getTweets(String containsText) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void maxResults(int limit) {
		this.limit = limit;
	}

	public void setDuration(int minutes) {
		this.minutes = minutes;
	}

}
