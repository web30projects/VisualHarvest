package visualharvester.service;

/**
 * Simple Key-Value Pair object
 */
public class Pair
{
   /** String defining key string */
   String key;
   /** String defining value string */
   String value;

   /**
    * Pair Constructor
    *
    * @param key
    *           String
    * @param value
    *           String
    */
   public Pair(final String key, final String value)
   {
      this.key = key;
      this.value = value;
   }

   /**
    * Key String getter
    *
    * @return String
    */
   public String getKey()
   {
      return key;
   }

   /**
    * Value String getter
    *
    * @return String
    */
   public String getValue()
   {
      return value;
   }

   /**
    * Key String Setter
    *
    * @param key
    *           String
    */
   public void setKey(final String key)
   {
      this.key = key;
   }

   /**
    * Value String setter
    *
    * @param value
    *           String
    */
   public void setValue(final String value)
   {
      this.value = value;
   }

}
