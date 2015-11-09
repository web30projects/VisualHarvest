package visualharvester.extractors;

import static org.junit.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestUrlExtractor {

	Logger log = Logger.getLogger(getClass());

	@Test
	public void testExtractUrl() {
		log.debug("testExtractUrl");

		final UrlExtractor urlExtractor = new UrlExtractor();
		final String url1 = urlExtractor.extractUrl("linkfollowingtexthttp://something");
		assertEquals("http://something", url1);
		final String url2 = urlExtractor.extractUrl("http://somethingasdfasdfasdfasd");
		assertEquals("http://somethingasdfasdfasdfasd", url2);
		final String url3 = urlExtractor.extractUrl("http://something text following link");
		assertEquals("http://something", url3);
		final String url4 = urlExtractor.extractUrl("http://www.google.com plain link");
		assertEquals("http://www.google.com", url4);
		final String url5 = urlExtractor.extractUrl("http://www.google.com. link with trailing . character");
		assertEquals("http://www.google.com", url5);
		final String url6 = urlExtractor.extractUrl("http://www.google.com! link with trailing ! character");
		assertEquals("http://www.google.com", url6);
		final String url7 = urlExtractor.extractUrl("http://www.google.com? link with trailing ? character");
		assertEquals("http://www.google.com", url7);

		final String url8 = urlExtractor.extractUrl("no links here");
		assertEquals("", url8);
		final String url9 = urlExtractor.extractUrl("https-schema present sans proper URL");
		assertEquals("", url9);

	}

}
