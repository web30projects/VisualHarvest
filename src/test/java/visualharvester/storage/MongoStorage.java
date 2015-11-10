package visualharvester.storage;

import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import visualharvester.objects.Location;
import visualharvester.objects.Tweet;

public class MongoStorage {

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

	private void insertTweet(Tweet tweet) {
		final Document document = new Document();
		document.put("text", tweet.getText());
		document.put("id", tweet.getId());
		document.put("url", tweet.getTweetUrl());

		final List<String> imageUrls = tweet.getImageUrls();
		final String[] imageUrlArray = new String[imageUrls.size()];
		imageUrls.toArray(imageUrlArray);
		document.put("images", imageUrlArray);

		final Location location = tweet.getLocation();
		final Document loc = new Document();
		loc.put("latitude", location.getLatitude());
		loc.put("longitude", location.getLongitude());
		document.put("loc", loc);

		collection.insertOne(document);
	}

	public void insertTweets(List<Tweet> tweets) {
		client = new MongoClient(hostname, port);
		database = client.getDatabase(databaseName);
		collection = database.getCollection(collectionName);

		for (final Tweet tweet : tweets) {
			insertTweet(tweet);
		}
	}

}
