package visualharvester.sources;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import twitter4j.Status;
import twitter4j.TwitterStreamFactory;

public class TestStreamTweetSource {

	Logger log = Logger.getLogger(getClass());

	@Test
	public void testGetTweets() {
		log.debug("testGetTweets");
		final StreamTweetSource source = new StreamTweetSource(TwitterStreamFactory.getSingleton());
		source.sourceLimit(15);
		final List<Status> tweets = source.getTweets("a");

		assertTrue(tweets.size() > 0);

		for (final Status status : tweets) {
			log.debug(status.getText());
		}
	}
}
