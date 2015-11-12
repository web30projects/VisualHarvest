package visualharvester.extractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.xml.sax.SAXException;

import visualharvester.objects.Location;
import visualharvester.objects.Tweet;

public class TestNearbyArticleExtractor {

	Logger log = Logger.getLogger(getClass());

	@Test
	public void testGetNearbyArticles_Coordinates() throws ParserConfigurationException, IOException, SAXException {
		log.debug("testGetNearbyArticles_Coordinates");

		final Tweet testTweet = new Tweet();
		final Location testLocation = new Location();
		testLocation.setInitialized(true);
		testLocation.setLatitude(37.786971);
		testLocation.setLongitude(-122.399677);
		testTweet.setLocation(testLocation);

		final NearbyArticleExtractor nearby = new NearbyArticleExtractor();
		final List<String> nearbyArticles = nearby.getNearbyArticles(testTweet);

		assertTrue(nearbyArticles.contains("Wikimedia Foundation"));
		assertTrue(nearbyArticles.contains("140 New Montgomery"));
		assertTrue(nearbyArticles.contains("New Montgomery Street"));
		assertTrue(nearbyArticles.contains("Cartoon Art Museum"));
		assertTrue(nearbyArticles.contains("Academy of Art University"));
		assertTrue(nearbyArticles.contains("San Francisco Planning and Urban Research Association"));
		assertTrue(nearbyArticles.contains("101 Second Street"));
		assertTrue(nearbyArticles.contains("222 Second Street"));
		assertTrue(nearbyArticles.contains("The Montgomery (San Francisco)"));
		assertTrue(nearbyArticles.contains("California Historical Society"));

		assertEquals(10, nearbyArticles.size());
	}

	@Test
	public void testGetNearbyArticles_Title() throws ParserConfigurationException, IOException, SAXException {
		log.debug("testGetNearbyArticles_Title");

		final NearbyArticleExtractor nearby = new NearbyArticleExtractor();
		final List<String> nearbyArticles = nearby.getNearbyArticles("Wikimedia Foundation");

		assertTrue(!nearbyArticles.contains("Wikimedia Foundation"));
		assertTrue(nearbyArticles.contains("140 New Montgomery"));
		assertTrue(nearbyArticles.contains("New Montgomery Street"));
		assertTrue(nearbyArticles.contains("Cartoon Art Museum"));
		assertTrue(nearbyArticles.contains("Academy of Art University"));
		assertTrue(nearbyArticles.contains("San Francisco Planning and Urban Research Association"));
		assertTrue(nearbyArticles.contains("101 Second Street"));
		assertTrue(nearbyArticles.contains("222 Second Street"));
		assertTrue(nearbyArticles.contains("The Montgomery (San Francisco)"));
		assertTrue(nearbyArticles.contains("California Historical Society"));
		assertTrue(nearbyArticles.contains("St. Regis Museum Tower"));

		assertEquals(10, nearbyArticles.size());
	}

}
