package visualharvester.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

@Path("api")
public class RestHandler {

	Logger log = Logger.getLogger(getClass());

	@Path("extract")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAugmentedTweets() {
		log.debug("GET: getAugmentedTweets");

		final TweetList jsonTweets = new TweetList(filteredList);
		return Response.ok(jsonTweets).type(MediaType.APPLICATION_JSON_TYPE).build();
	}

	@Path("test")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getTest() {
		log.debug("GET: test");
		return "test success";
	}
}
