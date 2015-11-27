package visualharvester.process;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import twitter4j.TwitterFactory;
import twitter4j.TwitterStreamFactory;
import visualharvester.objects.Tweet;
import visualharvester.sources.SearchTweetSource;
import visualharvester.sources.StreamTweetSource;
import visualharvester.sources.TweetSource;
import visualharvester.storage.MongoStorage;
import visualharvester.storage.Storage;

public class TestProcessor {

	static Storage store;

	@AfterClass
	public static void afterClass() {
		store.empty();
	}

	@BeforeClass
	public static void beforeClass() {
		store = new MongoStorage("localhost", 27017, "testdb", "testcollection");
	}

	@Test
	@Ignore
	public void testAugmentTweets_SearchAPI() {
		final TweetSource source = new SearchTweetSource(TwitterFactory.getSingleton());
		source.sourceLimit(20);
		source.disableRetweets();
		final Processor processor = new Processor(source);
		processor.setStore(store);
		final List<Tweet> augmentTweets = processor.augmentTweets("mesos", true);

		assertNotNull(augmentTweets);
	}

	@Test
	public void testAugmentTweets_StreamAPI() {
		final TweetSource source = new StreamTweetSource(TwitterStreamFactory.getSingleton());
		source.sourceLimit(7);
		source.disableRetweets();
		final Processor processor = new Processor(source);
		processor.setStore(store);
		final List<Tweet> augmentTweets = processor.augmentTweets("", true);

		assertNotNull(augmentTweets);
	}

}
