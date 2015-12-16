package visualharvester.storage;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.Document;

import visualharvester.objects.Location;
import visualharvester.objects.Tweet;

import com.mongodb.BasicDBList;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * MongoDB based Storage Implementation
 */
public class MongoStorage implements Storage
{
   /** The Logger */
   Logger log = Logger.getLogger(getClass());

   /** MongoDB Client object */
   MongoClient client;
   /** MongoDB Database object */
   MongoDatabase database;
   /** MongoDB Collection object */
   MongoCollection<Document> collection;
   /** MongoDB instance hostname */
   String hostname;
   /** MongoDB instance port value */
   int port;
   /** MongoDB Database name */
   String databaseName;
   /** MongoDB Collection name */
   String collectionName;

   /**
    * Constructor
    *
    * @param hostname
    *           String
    * @param port
    *           int
    * @param databaseName
    *           String
    * @param collectionName
    *           String
    */
   public MongoStorage(final String hostname, final int port, final String databaseName, final String collectionName)
   {
      this.hostname = hostname;
      this.port = port;
      this.databaseName = databaseName;
      this.collectionName = collectionName;

      client = new MongoClient(hostname, port);
      database = client.getDatabase(databaseName);
      collection = database.getCollection(collectionName);
   }

   @Override
   public void clearTweets(final String queryName)
   {
      final Document deleteCriteria = new Document();
      deleteCriteria.put("query", queryName);
      collection.deleteMany(deleteCriteria);
   }

   @Override
   public void close()
   {
      client.close();

   }

   @Override
   public void empty()
   {
      database.drop();
   }

   @Override
   public List<Tweet> getTweets(final String queryName)
   {

      collection = database.getCollection(collectionName);

      final Document getCriteria = new Document();
      getCriteria.put("query", queryName);

      final FindIterable<Document> find = collection.find(getCriteria);

      final List<Tweet> tweets = new ArrayList<>();
      for (final Document doc : find)
      {
         final Tweet tweet = new Tweet();
         tweet.setText(doc.getString("text"));
         tweet.setTweetUrl(doc.getString("url"));
         tweet.setId(doc.getString("tweetId"));

         final Location loc = new Location();
         loc.setInitialized(true);

         final Document locationDoc = doc.get("loc", Document.class);
         loc.setLatitude(locationDoc.getDouble("latitude").doubleValue());
         loc.setLongitude(locationDoc.getDouble("longitude").doubleValue());
         tweet.setLocation(loc);

         final List<String> imageUrls = new ArrayList<>();
         final List<?> urls = doc.get("images", List.class);

         for (final Object object : urls)
         {
            imageUrls.add(object.toString());
         }
         tweet.setImageUrls(imageUrls);

         final List<String> entities = new ArrayList<>();
         final List<?> entityList = doc.get("entities", List.class);
         for (final Object object : entityList)
         {
            entities.add(object.toString());
         }
         tweet.setExtractedEntities(entities);

         tweets.add(tweet);
      }

      return tweets;
   }

   @Override
   public void insertTweet(final Tweet tweet, final String queryName)
   {
      final Document document = new Document();
      document.put("text", tweet.getText());
      document.put("tweetId", tweet.getId());
      document.put("url", tweet.getTweetUrl());
      document.put("query", queryName);

      final List<String> imageUrls = tweet.getImageUrls();
      final BasicDBList urlList = new BasicDBList();
      for (final String url : imageUrls)
      {
         urlList.add(url);
      }
      document.put("images", urlList);

      final BasicDBList entityList = new BasicDBList();
      final List<String> entities = tweet.getExtractedEntities();
      for (final String entity : entities)
      {
         entityList.add(entity);
      }
      document.put("entities", entityList);

      final Location location = tweet.getLocation();

      if (location.isInitialized())
      {
         final Document loc = new Document();
         loc.put("latitude", new Double(location.getLatitude()));
         loc.put("longitude", new Double(location.getLongitude()));
         document.put("loc", loc);
      }
      else
      {
         return;
      }

      try
      {
         collection.insertOne(document);
      }
      catch (final Exception e)
      {
         log.error(e);
      }
   }

   @Override
   public void storeTweets(final List<Tweet> tweets, final String queryName)
   {
      for (final Tweet tweet : tweets)
      {
         insertTweet(tweet, queryName);
      }
   }

}
