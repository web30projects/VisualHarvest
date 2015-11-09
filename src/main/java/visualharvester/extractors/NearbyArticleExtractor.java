package visualharvester.extractors;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class NearbyArticleExtractor {

	private static final int resultLimit = 10;
	private static final int radiusMeters = 1000;
	private static final String coordinateSeparator = "%7C";
	private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	Logger log = Logger.getLogger(getClass());
	private DocumentBuilder builder;

	public List<String> getNearbyArticles(double latitude, double longitude) {

		log.debug("Getting Nearby Articles via Coordinates");
		final List<String> articleTitles = new ArrayList<>();

		try {
			builder = factory.newDocumentBuilder();
			final String query = getQuery(radiusMeters, latitude, longitude);
			log.debug("Query: " + query);

			final URL url = new URL(query);
			final URLConnection connection = url.openConnection();
			final Document document = builder.parse(connection.getInputStream());

			final NodeList results = document.getElementsByTagName("gs");
			for (int i = 0; i < results.getLength(); i++) {
				final Node node = results.item(i);
				final NamedNodeMap attributes = node.getAttributes();
				final Node namedItem = attributes.getNamedItem("title");
				articleTitles.add(namedItem.getNodeValue());
			}
		} catch (final ParserConfigurationException e) {
			log.error("Error Creating Document Parser", e);
		} catch (final UnsupportedEncodingException e) {
			log.error("Error URL Encoding query string", e);
		} catch (final MalformedURLException e) {
			log.error("Error Creating URL object", e);
		} catch (final IOException e) {
			log.error("Error creating URL Connection", e);
		} catch (final SAXException e) {
			log.error("Error parsing XML response", e);
		}
		return articleTitles;
	}

	public List<String> getNearbyArticles(String pageTitle) {

		log.debug("Getting Nearby Articles via Article Title");
		final List<String> articleTitles = new ArrayList<>();

		try {
			builder = factory.newDocumentBuilder();

			final String query = getQuery(radiusMeters, pageTitle);
			log.debug("Query: " + query);

			final URL url = new URL(query);
			final URLConnection connection = url.openConnection();
			final Document document = builder.parse(connection.getInputStream());

			final NodeList results = document.getElementsByTagName("gs");
			for (int i = 0; i < results.getLength(); i++) {
				final Node node = results.item(i);
				final NamedNodeMap attributes = node.getAttributes();
				final Node namedItem = attributes.getNamedItem("title");
				articleTitles.add(namedItem.getNodeValue());
			}
		} catch (final ParserConfigurationException e) {
			log.error("Error Creating Document Parser", e);
		} catch (final UnsupportedEncodingException e) {
			log.error("Error URL Encoding query string", e);
		} catch (final MalformedURLException e) {
			log.error("Error Creating URL object", e);
		} catch (final IOException e) {
			log.error("Error creating URL Connection", e);
		} catch (final SAXException e) {
			log.error("Error parsing XML response", e);
		}

		return articleTitles;

	}

	private String getQuery(int radiusMeters, double latitude, double longitude) {
		final StringBuilder sb = new StringBuilder();

		sb.append("https://en.wikipedia.org/w/api.php?action=query&format=xml&list=geosearch&gsradius=");
		sb.append(radiusMeters);
		sb.append("&gscoord=");
		sb.append(latitude);
		sb.append(coordinateSeparator);
		sb.append(longitude);
		sb.append("&gslimit=");
		sb.append(resultLimit);

		return sb.toString();
	}

	private String getQuery(int radiusMeters, String pageTitle) throws UnsupportedEncodingException {
		final StringBuilder sb = new StringBuilder();

		sb.append("https://en.wikipedia.org/w/api.php?action=query&format=xml&list=geosearch&gsradius=");
		sb.append(radiusMeters);
		sb.append("&gslimit=");
		sb.append(resultLimit);
		sb.append("&gspage=");
		sb.append(URLEncoder.encode(pageTitle, "UTF-8"));

		return sb.toString();
	}

}
