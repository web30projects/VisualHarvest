package visualharvester.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;

import twitter4j.TwitterFactory;
import visualharvester.objects.Location;
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

	static Storage store = null;
	private String host;
	private int port;
	private String database;

	private String collection;

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

	@Path("fetch/{query}/{limit}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response fetchTweets(@PathParam("query") String query, @PathParam("limit") String limit) {
		log.debug("GET: augmentTweets: " + query + "\tlimit string: " + limit);

		final TweetList list = new TweetList();

		final int limitValue = Integer.parseInt(limit);

		final MongoClient client = new MongoClient("74.140.208.12", 6789);
		final MongoCollection<Document> coll = client.getDatabase("visualdb").getCollection("visualcollection");

		final Document queryDoc = new Document();
		queryDoc.put("query", query);

		final FindIterable<Document> results = coll.find(queryDoc);

		int count = 0;
		for (final Document doc : results) {
			count++;

			final Tweet tweet = new Tweet();
			tweet.setText(doc.getString("text"));
			tweet.setTweetUrl(doc.getString("url"));
			tweet.setId(doc.getString("tweetId"));

			final Location loc = new Location();
			loc.setInitialized(true);

			final Document locationDoc = doc.get("loc", Document.class);
			loc.setLatitude(locationDoc.getDouble("latitude"));
			loc.setLongitude(locationDoc.getDouble("longitude"));
			tweet.setLocation(loc);

			final List<String> imageUrls = new ArrayList<>();
			final List<?> urls = doc.get("images", List.class);

			for (final Object object : urls) {
				imageUrls.add(object.toString());
			}
			tweet.setImageUrls(imageUrls);

			final List<String> entities = new ArrayList<>();
			final List<?> entityList = doc.get("entities", List.class);
			for (final Object object : entityList) {
				entities.add(object.toString());
			}
			tweet.setExtractedEntities(entities);
			list.getTweets().add(tweet);

			if (count > limitValue) {
				break;
			}
		}

		return Response.ok(list).build();
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

	private void initializeStore() {
		log.debug("initializing MongoStore");
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("visualharvester.properties")) {
			final Properties properties = new Properties();
			properties.load(is);

			host = properties.get("mongo.host").toString();
			final String portString = properties.get("mongo.port").toString();
			port = Integer.valueOf(portString);
			database = properties.get("mongo.database").toString();
			collection = properties.get("mongo.collection").toString();

		} catch (final IOException e) {
			log.error("Could not open properties file, using defaults", e);
			host = "localhost";
			port = 27017;
			database = "visualdb";
			collection = "visualcollection";
		}

		store = new MongoStorage(host, port, database, collection);

	}
}
