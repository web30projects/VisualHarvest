package visualharvester.objects;

/**
 * Location POJO
 */
public class Location
{

   /** flag if the location has been initialized */
   boolean initialized;
   /** Latitude Value */
   double latitude;
   /** Longitude Value */
   double longitude;

   /**
    * Location Constructor
    */
   public Location()
   {
      this.initialized = false;
   }

   /**
    * Location Parameterized Constructor
    *
    * @param latitude
    *           double
    * @param longitude
    *           double
    */
   public Location(final double latitude, final double longitude)
   {
      this.latitude = latitude;
      this.longitude = longitude;
      this.initialized = true;
   }

   /**
    * Latitude Getter
    *
    * @return latitude
    */
   public double getLatitude()
   {
      return latitude;
   }

   /**
    * Longitude Getter
    *
    * @return longitude
    */
   public double getLongitude()
   {
      return longitude;
   }

   /**
    * Is this Location initialized
    *
    * @return boolean true if initialized, false if not
    */
   public boolean isInitialized()
   {
      return initialized;
   }

   /**
    * Setter for the initialized flag
    *
    * @param initialized
    *           boolean
    */
   public void setInitialized(final boolean initialized)
   {
      this.initialized = initialized;
   }

   /**
    * Latitude Setter
    *
    * @param latitude
    *           double
    */
   public void setLatitude(final double latitude)
   {
      this.latitude = latitude;
   }

   /**
    * Longitude Setter
    *
    * @param longitude
    *           double
    */
   public void setLongitude(final double longitude)
   {
      this.longitude = longitude;
   }
}
