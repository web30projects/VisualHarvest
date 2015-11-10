package visualharvester.sources;

import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.TwitterStream;

public class StreamTweetSource implements TweetSource {

	Logger log = Logger.getLogger(getClass());

	private int limit = 50;
	private final TwitterStream stream;
	private boolean allowRetweets = true;

	public StreamTweetSource(TwitterStream stream) {
		this.stream = stream;
	}

	@Override
	public void disableRetweets() {
		this.allowRetweets = false;
	}

	@Override
	public List<Status> getTweets(String containsText) {
		final StreamListener listener;

		if (allowRetweets) {
			listener = new StreamListener(containsText);
		} else {
			listener = new StreamListener(containsText + " -RT");
		}

		stream.addListener(listener);

		try {
			stream.sample("en");
			Thread.sleep(limit * 1000);
		} catch (final InterruptedException e) {
			log.error(e);
		}

		stream.shutdown();
		final List<Status> list = listener.getTweets();
		log.debug("Received " + list.size() + " matching tweets");
		return list;
	}

	@Override
	public void sourceLimit(int limit) {
		this.limit = limit;
	}

}
