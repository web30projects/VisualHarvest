package visualharvester.service;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

public abstract class BasicService {

	protected class ServiceBinder extends AbstractBinder {
		ServiceConfig config = new ServiceConfig();

		public ServiceBinder(Pair... pairs) {
			for (Pair p : pairs) {
				config.put(p);
			}
		}

		@Override
		protected void configure() {
			bind(config).to(ServiceConfig.class);
		}
	}

	protected URI serviceUri = null;
	protected UriBuilder uriBuilder = UriBuilder.fromUri("http://{host}:{port}/");

	public String getUri() {
		if (serviceUri == null) {
			return "";
		}

		return serviceUri.toString();
	}


}
