package visualharvester.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Jersey Service Configuration Object
 */
public class ServiceConfig
{

   /** List of Key-Value Pairs */
   List<Pair> pairs = new ArrayList<>();

   /**
    * Get Method to obtain the Value for a contained K-V pair
    *
    * @param key
    *           String
    * @return String or null if no key matches the input String
    */
   public String get(final String key)
   {
      for (final Pair pair : pairs)
      {
         if (pair.getKey().equals(key))
         {
            return pair.getValue();
         }
      }
      return null;
   }

   /**
    * Put Method to store a K-V pair (Pair input)
    *
    * @param newPair
    *           Pair
    */
   public void put(final Pair newPair)
   {
      for (final Pair pair : pairs)
      {
         if (pair.getKey().equals(newPair.getKey()))
         {
            return;
         }
      }
      pairs.add(newPair);
   }

   /**
    * Put Method to store a K-V pair (individual Strings for input)
    *
    * @param key
    *           String
    * @param value
    *           String
    */
   public void put(final String key, final String value)
   {
      pairs.add(new Pair(key, value));
   }

}
