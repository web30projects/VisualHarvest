package org.glassfish.jersey.grizzly2.httpserver;

import org.glassfish.jersey.server.ResourceConfig;

// Utility Class to enable the Grizzly application server to behave
// as both application server and static file server
public class GrizzlyUtil {

	public static GrizzlyHttpContainer getContainer(ResourceConfig rc) {
		return new GrizzlyHttpContainer(rc);
	}

}
