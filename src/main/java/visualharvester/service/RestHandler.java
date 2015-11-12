package visualharvester.service;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import twitter4j.TwitterFactory;
import visualharvester.objects.Tweet;
import visualharvester.process.Processor;
import visualharvester.sources.SearchTweetSource;
import visualharvester.sources.TweetSource;
import visualharvester.storage.MongoStorage;
import visualharvester.storage.Storage;

@Path("api")
public class RestHandler {

	static Storage store = new MongoStorage("localhost", 27017, "visualdb", "demo");

	Logger log = Logger.getLogger(getClass());

	@Path("extract")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAugmentedTweets(@QueryParam("query") String query) {
		log.debug("GET: getAugmentedTweets");

		final TweetSource source = new SearchTweetSource(TwitterFactory.getSingleton());
		source.sourceLimit(2);
		source.disableRetweets();
		final Processor processor = new Processor(source);
		processor.setStore(store);
		final List<Tweet> augmentTweets = processor.augmentTweets(query, true);
		final TweetList jsonTweets = new TweetList(augmentTweets);
		return Response.ok(jsonTweets).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Path("test")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getTest() {
		log.debug("GET: test");
		return "test success";
	}
}
