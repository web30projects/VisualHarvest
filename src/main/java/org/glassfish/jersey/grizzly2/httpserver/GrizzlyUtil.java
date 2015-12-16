package org.glassfish.jersey.grizzly2.httpserver;

import org.glassfish.jersey.server.ResourceConfig;

/**
 * Utility class allowing Grizzly application server to also perform as a static web server
 */
public class GrizzlyUtil
{

   /**
    * Method to obtain a Grizzly HTTP Container using a protected constructor
    *
    * @param rc
    *           ResourceConfig Jersey REST Resource Configuration object
    * @return GrizzlyHTTPContainer
    */
   public static GrizzlyHttpContainer getContainer(final ResourceConfig rc)
   {
      return new GrizzlyHttpContainer(rc);
   }

}
