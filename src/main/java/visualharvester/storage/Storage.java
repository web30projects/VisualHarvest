package visualharvester.storage;

import java.util.List;

import visualharvester.objects.Tweet;

/**
 * Tweet Storage Interface
 */
public interface Storage
{
   /**
    * Method to remove all tweets matching a given criteria from the Store
    *
    * @param criteria
    *           String
    */
   public void clearTweets(String criteria);

   /**
    * Close the Store connection
    */
   public void close();

   /**
    * Method to remove all tweets from the store
    */
   public void empty();

   /**
    * Method to obtain a list of tweets from the store
    *
    * @param criteria
    *           String
    * @return List<Tweet>
    */
   public List<Tweet> getTweets(String criteria);

   /**
    * Method to insert a Tweet to the store
    *
    * @param tweet
    *           Tweet object
    * @param criteria
    *           String
    */
   public void insertTweet(Tweet tweet, String criteria);

   /**
    * Method to insert a list of Tweets into the store
    *
    * @param tweets
    *           List<Tweet>
    * @param criteria
    *           String
    */
   public void storeTweets(List<Tweet> tweets, String criteria);

}
