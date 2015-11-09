package visualharvester.process;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import twitter4j.TwitterFactory;
import visualharvester.objects.Tweet;
import visualharvester.sources.SearchTweetSource;

public class TestProcessor {

	@Test
	public void testAugmentTweets() {
		final Processor processor = new Processor(new SearchTweetSource(TwitterFactory.getSingleton()));
		final List<Tweet> augmentTweets = processor.augmentTweets("beach");

		assertNotNull(augmentTweets);
	}

}
