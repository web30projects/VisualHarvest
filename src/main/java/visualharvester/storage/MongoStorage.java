package visualharvester.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import visualharvester.objects.Location;
import visualharvester.objects.Tweet;

public class MongoStorage implements Storage {

	Logger log = Logger.getLogger(getClass());
	MongoClient client;
	MongoDatabase database;

	String hostname;
	int port;

	String databaseName;

	public MongoStorage(String hostname, int port, String databaseName) {
		this.hostname = hostname;
		this.port = port;
		this.databaseName = databaseName;

		client = new MongoClient(hostname, port);
		database = client.getDatabase(databaseName);
	}

	@Override
	public void clearTweets(String collectionName) {
		client.getDatabase(databaseName).getCollection(collectionName).drop();

	}

	@Override
	public void close() {
		client.close();

	}

	@Override
	public void empty() {
		database.drop();
	}

	@Override
	public List<Tweet> getTweets(String collectionName) {

		final MongoCollection<Document> collection = database.getCollection(collectionName);
		final FindIterable<Document> find = collection.find();

		final List<Tweet> tweets = new ArrayList<>();
		for (final Document doc : find) {
			final Tweet tweet = new Tweet();
			tweet.setText(doc.getString("text"));
			tweet.setTweetUrl(doc.getString("url"));
			tweet.setId(doc.getLong("tweetId"));

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

			tweets.add(tweet);
		}

		return tweets;
	}

	private void insertTweet(Tweet tweet, MongoCollection<Document> collection) {
		final Document document = new Document();
		document.put("text", tweet.getText());
		document.put("tweetId", tweet.getId());
		document.put("url", tweet.getTweetUrl());

		final List<String> imageUrls = tweet.getImageUrls();
		final BasicDBList urlList = new BasicDBList();
		for (final String url : imageUrls) {
			urlList.add(url);
		}
		document.put("images", urlList);

		final BasicDBList entityList = new BasicDBList();
		final List<String> entities = tweet.getExtractedEntities();
		for (final String entity : entities) {
			entityList.add(entity);
		}
		document.put("entities", entityList);

		final Location location = tweet.getLocation();

		if (location.isInitialized()) {
			final Document loc = new Document();
			loc.put("latitude", location.getLatitude());
			loc.put("longitude", location.getLongitude());
			document.put("loc", loc);
		} else {
			return;
		}

		try {
			collection.insertOne(document);
		} catch (final Exception e) {
			log.error(e);
		}
	}

	@Override
	public void storeTweets(List<Tweet> tweets, String collectionName) {
		for (final Tweet tweet : tweets) {
			insertTweet(tweet, database.getCollection(collectionName));
		}
	}

}
