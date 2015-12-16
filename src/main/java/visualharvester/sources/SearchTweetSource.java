package visualharvester.sources;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Tweet Source against the Twitter Search API
 */
public class SearchTweetSource implements TweetSource
{
   /** The Logger */
   Logger log = Logger.getLogger(getClass());

   /** Twitter4J instance */
   private final Twitter twitter;
   /** The Limit for Searching (number of tweets to request) */
   private int limit = 50;
   /** flag for allowing retweets */
   private boolean allowRetweets = true;

   /**
    * Constructor
    *
    * @param twitter
    *           Twitter4J instance
    */
   public SearchTweetSource(final Twitter twitter)
   {
      this.twitter = twitter;
   }

   @Override
   public void disableRetweets()
   {
      allowRetweets = false;
   }

   @Override
   public List<Status> getTweets(final String containsText)
   {
      log.debug("Obtaining " + limit + " tweets via Twitter Search API");
      boolean searchComplete = false;
      long tweetCount = 0;

      final Query query;

      if (allowRetweets)
      {
         query = new Query(containsText);
      }
      else
      {
         query = new Query(containsText + " -RT");
      }

      final List<Status> allTweets = new ArrayList<>();
      query.setLang("en");

      if (limit < 100)
      {
         try
         {
            query.setCount(limit);
            final QueryResult queryResult = twitter.search(query);
            final List<Status> tweets = queryResult.getTweets();
            allTweets.addAll(tweets);
         }
         catch (final TwitterException e)
         {
            log.error("Error retrieving tweets from Twitter Search API", e);
         }
      }
      else
      {

         try
         {
            query.setCount(100);
            log.debug("Getting Tweets");
            QueryResult queryResult = twitter.search(query);
            List<Status> tweets = queryResult.getTweets();
            log.debug(queryResult.getRateLimitStatus().toString());

            if (tweets.size() == 0)
            {
               log.debug("No tweets found");
               searchComplete = true;
            }
            else
            {
               log.debug("obtained " + tweets.size() + " initial tweets");
            }

            while (!searchComplete)
            {
               tweetCount += tweets.size();
               allTweets.addAll(tweets);

               if (tweetCount >= limit)
               {
                  log.debug("Reached enough tweets");
                  searchComplete = true;
               }
               else
               {
                  log.debug("getting MORE tweets");
                  query.setMaxId(minId(tweets));
                  queryResult = twitter.search(query);
                  log.debug(queryResult.getRateLimitStatus().toString());
                  tweets = queryResult.getTweets();
                  if (tweets.size() == 0)
                  {
                     log.debug("no more tweets found");
                     searchComplete = true;
                  }
                  else
                  {
                     log.debug("found " + tweets.size() + " more tweets");
                  }
               }
            }
         }
         catch (final TwitterException e)
         {
            log.error("Error retrieving tweets from Twitter Search API", e);
         }
      }

      log.debug("Found " + allTweets.size() + " total tweets");
      return allTweets;
   }

   /**
    * Method to find the lowest ID value in a list of tweets
    *
    * @param tweets
    *           List<Status>
    * @return long id value
    */
   private long minId(final List<Status> tweets)
   {
      long minId = Long.MAX_VALUE;
      for (final Status status : tweets)
      {
         final long id = status.getId();
         if (id < minId)
         {
            minId = id;
         }
      }
      return minId;
   }

   @Override
   public void sourceLimit(final int limitValue)
   {
      this.limit = limitValue;
   }

}
