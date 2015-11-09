package visualharvester.sources;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

public class TestSearchTweetSource {

	Logger log = Logger.getLogger(getClass());

	private void printTweets(List<Status> tweets) {
		for (final Status status : tweets) {
			log.debug(status.getText());
		}
	}

	@Test
	public void testGetTweets() {
		log.debug("testGetTweets");
		final SearchTweetSource source = new SearchTweetSource(TwitterFactory.getSingleton());
		final List<Status> tweets = source.getTweets("linux");

		assertNotNull(tweets);
		assertTrue(tweets.size() > 0);

		printTweets(tweets);
	}

	@Test
	public void testTwitterError() throws TwitterException {
		log.debug("testTwitterError");
		final Twitter mockTwitter = Mockito.mock(Twitter.class);
		Mockito.when(mockTwitter.search(Mockito.any())).thenThrow(new TwitterException("Mocked Error"));

		final SearchTweetSource source = new SearchTweetSource(mockTwitter);
		final List<Status> tweets = source.getTweets("linux");

		assertNotNull(tweets);
		assertTrue(tweets.size() == 0);
	}
}
