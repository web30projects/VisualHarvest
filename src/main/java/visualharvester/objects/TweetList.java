package visualharvester.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Tweet List Class with JAX-B support
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TweetList
{

   /** List of Tweets */
   List<Tweet> tweets;

   /**
    * Constructor
    */
   public TweetList()
   {

   }

   /**
    * Parameterized Constructor
    *
    * @param tweets
    *           List<Tweet>
    */
   public TweetList(final List<Tweet> tweets)
   {
      this.tweets = tweets;
   }

   /**
    * List of Tweets Getter
    *
    * @return List<Tweet>
    */
   public List<Tweet> getTweets()
   {
      if (tweets == null)
      {
         tweets = new ArrayList<>();
      }
      return tweets;
   }

   /**
    * List of Tweets Setter
    *
    * @param tweets
    *           List<Tweet>
    */
   public void setTweets(final List<Tweet> tweets)
   {
      this.tweets = tweets;
   }

}
