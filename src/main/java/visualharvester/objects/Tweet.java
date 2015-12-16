package visualharvester.objects;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Tweet Class with JAX-B Support
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Tweet
{

   /** Tweet Text */
   String text = null;
   /** Tweet Identifier */
   String id = null;
   /** Tweet Location object */
   Location location = null;
   /** Tweet URL value */
   String tweetUrl = null;
   /** List of Image URLs */
   List<String> imageUrls = null;
   /** List of Extracted Entities */
   List<String> extractedEntities = null;

   /**
    * Constructor
    */
   public Tweet()
   {

   }

   /**
    * Extracted Entities Getter
    *
    * @return List<String>
    */
   public List<String> getExtractedEntities()
   {
      if (extractedEntities == null)
      {
         extractedEntities = new ArrayList<>();
      }
      return extractedEntities;
   }

   /**
    * Tweet ID Getter
    *
    * @return String
    */
   public String getId()
   {
      return id;
   }

   /**
    * Tweet Image URL List Getter
    *
    * @return List<String>
    */
   public List<String> getImageUrls()
   {
      if (imageUrls == null)
      {
         imageUrls = new ArrayList<>();
      }
      return imageUrls;
   }

   /**
    * Tweet Location Getter
    *
    * @return Location
    */
   public Location getLocation()
   {
      if (location == null)
      {
         location = new Location();
      }
      return location;
   }

   /**
    * Tweet Text Getter
    *
    * @return String
    */
   public String getText()
   {
      return text;
   }

   /**
    * Tweet URL Getter
    *
    * @return String
    */
   public String getTweetUrl()
   {
      return tweetUrl;
   }

   /**
    * Tweet Extracted Entities List Setter
    *
    * @param extractedEntities
    *           List<String>
    */
   public void setExtractedEntities(final List<String> extractedEntities)
   {
      this.extractedEntities = extractedEntities;
   }

   /**
    * Tweet ID Setter
    *
    * @param id
    *           String
    */
   public void setId(final String id)
   {
      this.id = id;
   }

   /**
    * Tweet Image URL List Setter
    *
    * @param imageUrls
    *           List<String>
    */
   public void setImageUrls(final List<String> imageUrls)
   {
      this.imageUrls = imageUrls;
   }

   /**
    * Tweet Location Setter
    *
    * @param location
    *           Location
    */
   public void setLocation(final Location location)
   {
      this.location = location;
   }

   /**
    * Tweet Text Setter
    *
    * @param text
    *           String
    */
   public void setText(final String text)
   {
      this.text = text;
   }

   /**
    * Tweet URL Setter
    *
    * @param tweetUrl
    *           String
    */
   public void setTweetUrl(final String tweetUrl)
   {
      this.tweetUrl = tweetUrl;
   }

}
