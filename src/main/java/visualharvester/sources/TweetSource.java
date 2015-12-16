package visualharvester.sources;

import java.util.List;

import twitter4j.Status;

/**
 * Interface for Sources of Tweets
 */
public interface TweetSource
{

   /**
    * Should disable obtaining of retweets
    */
   public void disableRetweets();

   /**
    * Method to get obtained Tweets
    *
    * @param containsText
    *           String criteria
    * @return List<Status>
    */
   List<Status> getTweets(String containsText);

   /**
    * Set the limit value for whatever source is used; can vary in behavior depending on Source implementations
    *
    * @param limit
    *           int
    */
   public void sourceLimit(int limit);
}
