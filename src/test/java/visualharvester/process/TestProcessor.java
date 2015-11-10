package visualharvester.process;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import twitter4j.TwitterFactory;
import visualharvester.objects.Tweet;
import visualharvester.sources.SearchTweetSource;
import visualharvester.sources.TweetSource;

public class TestProcessor {

	@Test
	public void testAugmentTweets_SearchAPI() {
		final TweetSource source = new SearchTweetSource(TwitterFactory.getSingleton());
		source.sourceLimit(100);
		final Processor processor = new Processor(source);
		final List<Tweet> augmentTweets = processor.augmentTweets("beach");

		assertNotNull(augmentTweets);
	}

	@Test
	public void testAugmentTweets_StreamAPI() {

	}

}
