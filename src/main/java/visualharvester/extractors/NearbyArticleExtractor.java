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

import visualharvester.objects.Location;
import visualharvester.objects.Tweet;

/**
 * Class for Extracting Nearby wikipedia articles
 */
public class NearbyArticleExtractor
{

   // TODO some of these default values and setters could be moved into configuration files
   /** Maximum number of nearby articles to obtain */
   private static final int resultLimit = 10;
   /** Definition of "nearby": 1 kilometer */
   private static final int radiusMeters = 1000;
   /** Coordinate Separation character defined by the Wikipedia geospatial search API */
   private static final String coordinateSeparator = "%7C";

   /** JSoup Document Builder Factory */
   private static final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
   /** JSoup Document Builder */
   private DocumentBuilder builder;

   /** The Logger */
   Logger log = Logger.getLogger(getClass());

   /**
    * Method to extract nearby articles given a wikipedia page/article title
    *
    * @param pageTitle
    *           String containing the page/article title
    * @return List<String> containing nearby article names
    */
   public List<String> getNearbyArticles(final String pageTitle)
   {

      final List<String> articleTitles = new ArrayList<>();

      try
      {
         builder = factory.newDocumentBuilder();

         final String query = getQuery(pageTitle);

         final URL url = new URL(query);
         final URLConnection connection = url.openConnection();
         final Document document = builder.parse(connection.getInputStream());

         final NodeList results = document.getElementsByTagName("gs");
         for (int i = 0; i < results.getLength(); i++)
         {
            final Node node = results.item(i);
            final NamedNodeMap attributes = node.getAttributes();
            final Node namedItem = attributes.getNamedItem("title");
            articleTitles.add(namedItem.getNodeValue());
         }
      }
      catch (final ParserConfigurationException e)
      {
         log.error("Error Creating Document Parser", e);
      }
      catch (final UnsupportedEncodingException e)
      {
         log.error("Error URL Encoding query string", e);
      }
      catch (final MalformedURLException e)
      {
         log.error("Error Creating URL object", e);
      }
      catch (final IOException e)
      {
         log.error("Error creating URL Connection", e);
      }
      catch (final SAXException e)
      {
         log.error("Error parsing XML response", e);
      }

      return articleTitles;

   }

   /**
    * Method to obtain a listing of nearby articles for a given tweet
    *
    * @param tweet
    *           Tweet object providing location
    * @return List<String> containing nearby articles
    */
   public List<String> getNearbyArticles(final Tweet tweet)
   {

      final List<String> articleTitles = new ArrayList<>();

      try
      {
         builder = factory.newDocumentBuilder();
         final String query = getQuery(tweet.getLocation());

         final URL url = new URL(query);
         final URLConnection connection = url.openConnection();
         final Document document = builder.parse(connection.getInputStream());

         final NodeList results = document.getElementsByTagName("gs");
         for (int i = 0; i < results.getLength(); i++)
         {
            final Node node = results.item(i);
            final NamedNodeMap attributes = node.getAttributes();
            final Node namedItem = attributes.getNamedItem("title");
            articleTitles.add(namedItem.getNodeValue());
         }
      }
      catch (final ParserConfigurationException e)
      {
         log.error("Error Creating Document Parser", e);
      }
      catch (final UnsupportedEncodingException e)
      {
         log.error("Error URL Encoding query string", e);
      }
      catch (final MalformedURLException e)
      {
         log.error("Error Creating URL object", e);
      }
      catch (final IOException e)
      {
         log.error("Error creating URL Connection", e);
      }
      catch (final SAXException e)
      {
         log.error("Error parsing XML response", e);
      }
      return articleTitles;
   }

   /**
    * Method to build a query string for Wikipedia's geospatial query API for a given Location
    *
    * @param location
    *           Location object containing geospatial coordinates
    * @return String containing the created query
    */
   private String getQuery(final Location location)
   {
      final StringBuilder sb = new StringBuilder();

      sb.append("https://en.wikipedia.org/w/api.php?action=query&format=xml&list=geosearch&gsradius=");
      sb.append(radiusMeters);
      sb.append("&gscoord=");
      sb.append(location.getLatitude());
      sb.append(coordinateSeparator);
      sb.append(location.getLongitude());
      sb.append("&gslimit=");
      sb.append(resultLimit);

      return sb.toString();
   }

   /**
    * Method to obtain a query string for Wikipedia's geospatial API for a given pageTitle
    *
    * @param pageTitle
    *           String containing the page/article title
    * @return String containing the created query
    * @throws UnsupportedEncodingException
    *            thrown if the provided pageTitle cannot be URL Encoded
    */
   private String getQuery(final String pageTitle) throws UnsupportedEncodingException
   {
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
