package visualharvester.storage;

import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import visualharvester.objects.Location;
import visualharvester.objects.Tweet;

public class MongoStorage implements Storage {

	Logger log = Logger.getLogger(getClass());
	MongoClient client;
	MongoDatabase database;

	MongoCollection<Document> collection;
	String hostname;
	int port;
	String collectionName;

	String databaseName;

	public MongoStorage(String hostname, int port, String databaseName, String collectionName) {
		this.hostname = hostname;
		this.port = port;
		this.databaseName = databaseName;
		this.collectionName = collectionName;
	}

	@Override
	public void empty() {
		collection.drop();
		database.drop();
	}

	private void insertTweet(Tweet tweet) {
		final Document document = new Document();
		document.put("text", tweet.getText());
		document.put("tweetId", tweet.getId());
		document.put("url", tweet.getTweetUrl());

		final List<String> imageUrls = tweet.getImageUrls();
		final BasicDBList list = new BasicDBList();
		for (final String url : imageUrls) {
			list.add(url);
		}
		document.put("images", list);

		final Location location = tweet.getLocation();

		if (location.isInitialized()) {
			final Document loc = new Document();
			loc.put("latitude", location.getLatitude());
			loc.put("longitude", location.getLongitude());
			document.put("loc", loc);
		}

		try {
			collection.insertOne(document);
		} catch (final Exception e) {
			log.error(e);
		}
	}

	@Override
	public void storeTweets(List<Tweet> tweets) {
		client = new MongoClient(hostname, port);
		database = client.getDatabase(databaseName);
		collection = database.getCollection(collectionName);

		for (final Tweet tweet : tweets) {
			insertTweet(tweet);
		}
	}

}
