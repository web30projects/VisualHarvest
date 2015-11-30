package visualharvester.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.ServerConfiguration;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpContainer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyUtil;
import org.glassfish.jersey.server.ResourceConfig;

public class RestService extends BasicService {

	static Logger log = Logger.getLogger(RestService.class);

	String host;
	int port;

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		final RestService service = new RestService();
		final HttpServer server = service.buildServer();

		try {
			server.start();
			Thread.currentThread().join();
		} catch (final Exception ioe) {
			System.err.println(ioe);
		} finally {
			server.stop();
		}

	}

	public RestService() {
		try (InputStream is = getClass().getClassLoader().getResourceAsStream("visualharvester.properties")) {
			Properties properties = new Properties();
			properties.load(is);

			host = properties.get("service.host").toString();
			String portString = properties.get("service.port").toString();
			port = Integer.valueOf(portString);

		} catch (IOException e) {
			log.error("Error opening properties file", e);
			host = "localhost";
			port = 4222;
		}

		serviceUri = uriBuilder.resolveTemplate("host", host).resolveTemplate("port", port).build();
	}

	public HttpServer buildServer() {
		final HttpServer httpServer = new HttpServer();
		final NetworkListener listener = new NetworkListener("listener", serviceUri.getHost(), serviceUri.getPort());
		httpServer.addListener(listener);

		final ServerConfiguration config = httpServer.getServerConfiguration();
		final CLStaticHttpHandler fileContainer = new CLStaticHttpHandler(RestService.class.getClassLoader());
		config.addHttpHandler(fileContainer, "/web");

		final ResourceConfig rc = new ResourceConfig().packages("visualharvester.service");
		final GrizzlyHttpContainer restContainer = GrizzlyUtil.getContainer(rc);
		config.addHttpHandler(restContainer, "/rest");
		return httpServer;
	}
}
