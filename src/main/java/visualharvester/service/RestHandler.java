package visualharvester.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import twitter4j.TwitterFactory;
import visualharvester.objects.Tweet;
import visualharvester.objects.TweetList;
import visualharvester.process.Processor;
import visualharvester.sources.SearchTweetSource;
import visualharvester.sources.TweetSource;
import visualharvester.storage.MongoStorage;
import visualharvester.storage.Storage;

@Path("api")
public class RestHandler {

	private class AugmentRunner implements Runnable {

		Processor processor;
		String criteria;
		boolean ignoreCoordinates;

		public AugmentRunner(Processor processor, String criteria, boolean ignoreCoordinates) {
			this.processor = processor;
			this.criteria = criteria;
			this.ignoreCoordinates = ignoreCoordinates;
		}

		@Override
		public void run() {
			processor.augmentTweets(criteria, ignoreCoordinates);
		}

	}

	private String host;
	private int port;
	private String database;
	private String collection;

	private void initializeStore() {
		log.debug("initializing MongoStore");
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("visualharvester.properties")) {
			Properties properties = new Properties();
			properties.load(is);

			host = properties.get("mongo.host").toString();
			String portString = properties.get("mongo.port").toString();
			port = Integer.valueOf(portString);
			database = properties.get("mongo.database").toString();
			collection = properties.get("mongo.collection").toString();

		} catch (IOException e) {
			log.error("Could not open properties file, using defaults", e);
			host = "localhost";
			port = 27017;
			database = "visualdb";
			collection = "visualcollection";
		}

		store = new MongoStorage(host, port, database, collection);

	}

	static Storage store = null;
	Logger log = Logger.getLogger(getClass());

	@Path("augment/{query}/{limit}")
	@GET
	public Response augmentTweets(@PathParam("query") String query, @PathParam("limit") String limit) {
		log.debug("GET: augmentTweets: " + query + "\tlimit string: " + limit);

		if (store == null) {
			initializeStore();
		}

		int limitValue = 20;

		try {
			limitValue = Integer.parseInt(limit);
		} catch (final Exception e) {
			log.error("could not parse provided limit value, using default");
		}

		final TweetSource source = new SearchTweetSource(TwitterFactory.getSingleton());
		source.sourceLimit(limitValue);
		source.disableRetweets();
		final Processor processor = new Processor(source);
		processor.setStore(store);

		final AugmentRunner runner = new AugmentRunner(processor, query, true);
		final ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(runner);

		return Response.ok().build();
	}

	@Path("clear")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response clearTweets() {
		log.debug("GET: clearTweets");
		if (store == null) {
			initializeStore();
		}
		store.empty();
		return Response.ok().build();
	}

	@Path("test")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getTest() {
		log.debug("GET: test");
		return "test success";
	}

	@Path("tweets/{query}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getTweets(@PathParam("query") String query) {
		log.debug("GET: getTweets");

		if (store == null) {
			initializeStore();
		}

		final List<Tweet> tweets = store.getTweets(query);
		final TweetList list = new TweetList();
		list.setTweets(tweets);
		return Response.ok(list).build();
	}
}
