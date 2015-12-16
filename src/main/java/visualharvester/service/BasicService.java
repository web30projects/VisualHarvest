package visualharvester.service;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * Basic Jersey Service Class
 */
public abstract class BasicService
{

   /**
    * Binder for passing Key-Value Pairs from Jersey Service classes to Jersey Resource classes
    */
   protected class ServiceBinder extends AbstractBinder
   {
      /** Service Configuration object */
      ServiceConfig config = new ServiceConfig();

      /**
       * Constructor
       *
       * @param pairs
       *           Pair parameters
       */
      public ServiceBinder(final Pair... pairs)
      {
         for (final Pair p : pairs)
         {
            config.put(p);
         }
      }

      @Override
      protected void configure()
      {
         bind(config).to(ServiceConfig.class);
      }
   }

   /** The Service's URI */
   protected URI serviceUri = null;
   /** The Service URI builder */
   protected UriBuilder uriBuilder = UriBuilder.fromUri("http://{host}:{port}/");

   /**
    * URI Getter
    *
    * @return String
    */
   public String getUri()
   {
      if (serviceUri == null)
      {
         return "";
      }

      return serviceUri.toString();
   }

}
