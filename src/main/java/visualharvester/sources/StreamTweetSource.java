package visualharvester.sources;

import java.util.List;

import org.apache.log4j.Logger;

import twitter4j.Status;
import twitter4j.TwitterStream;

/**
 * Tweet Source against the Twitter Stream API
 */
public class StreamTweetSource implements TweetSource
{

   /** The Logger */
   Logger log = Logger.getLogger(getClass());

   /** The Limit for Streaming (seconds to sample the stream) */
   private int limit = 50;
   /** Twitter4J Stream instance */
   private final TwitterStream stream;
   /** flag for allowing retweets */
   private boolean allowRetweets = true;

   /**
    * Constructor
    *
    * @param stream
    *           TwitterStream instance
    */
   public StreamTweetSource(final TwitterStream stream)
   {
      this.stream = stream;
   }

   @Override
   public void disableRetweets()
   {
      this.allowRetweets = false;
   }

   @Override
   public List<Status> getTweets(final String containsText)
   {
      final StreamListener listener;

      listener = new StreamListener(containsText, allowRetweets);

      stream.addListener(listener);

      try
      {
         stream.sample("en");
         Thread.sleep(limit * 1000);
      }
      catch (final InterruptedException e)
      {
         log.error(e);
      }

      stream.shutdown();
      final List<Status> list = listener.getTweets();
      log.debug("Received " + list.size() + " matching tweets");
      return list;
   }

   @Override
   public void sourceLimit(final int limitValue)
   {
      this.limit = limitValue;
   }

}
