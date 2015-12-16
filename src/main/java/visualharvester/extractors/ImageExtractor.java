package visualharvester.extractors;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.io.Files;

/**
 * Image Extraction Class
 */
public class ImageExtractor
{

   /** The Logger */
   Logger log = Logger.getLogger(getClass());

   /** Directory reference */
   File directory;

   /**
    * Array of Currently Supported File Extensions TODO include additional formats if needed/wanted TODO this could be
    * moved into the properties file
    */
   private final String[] imageExtensions =
   { ".jpg", ".jpeg", ".png", ".bmp" };

   /**
    * Image Extractor Constructor
    *
    * @param storageDirectoryPath
    *           String representing the path to store all processed images
    */
   public ImageExtractor(final String storageDirectoryPath)
   {
      directory = new File(storageDirectoryPath);
      if (!directory.exists())
      {
         directory.mkdirs();
      }
   }

   /**
    * Image Extractor's Process method: Using a given URL, the extractor pulls out all image-related HTML references
    * and, after downloading those files, determines which are relevant. At this time relevancy is simply ensuring the
    * image files are greater than a present file size
    *
    * @param sourceUrl
    *           String representing the HTTP resource to extract image URLs
    * @return List<String> A Listing of Image URL strings
    */
   public List<String> extractImageUrls(final String sourceUrl)
   {
      List<String> urls = new ArrayList<>();
      try
      {
         final Document document = getDocument(sourceUrl);
         urls = getImageUrls(document, getFinalURL(sourceUrl));

      }
      catch (final IOException e)
      {
         log.error("Error Extracting Image URLs from " + sourceUrl, e);
      }
      return urls;
   }

   /**
    * Method for obtaining a JSoup Document object
    *
    * @param url
    *           String URL linking to the HTTP Resource
    * @return Document JSoup Document
    * @throws IOException
    *            Thrown if GET request is refused
    */
   private Document getDocument(final String url) throws IOException
   {
      return Jsoup.connect(url).get();
   }

   /**
    * Method to extract the filename from a URL
    *
    * @param urlFilePath
    *           String containing the complete URL
    * @return String containing the filename
    */
   private String getFilename(final String urlFilePath)
   {
      String filename = urlFilePath.substring(urlFilePath.lastIndexOf("/") + 1);
      if (filename.contains(":"))
      {
         filename = filename.substring(filename.lastIndexOf(":") + 1);
      }
      return filename;
   }

   /**
    * Method to obtain the final URL after HTTP 300 Found style redirections (Recursive)
    *
    * @param url
    *           String containing the URL attempt to resolve to its final URL
    * @return String Containing the final resolved URL
    */
   private String getFinalURL(final String url)
   {
      try
      {
         final HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
         con.setInstanceFollowRedirects(false);
         con.connect();
         con.getInputStream();

         if (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM
               || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)
         {
            final String redirectUrl = con.getHeaderField("Location");
            return getFinalURL(redirectUrl);
         }

      }
      catch (final IOException e)
      {
         log.error("Error obtaining redirected URL", e);
      }
      return url;
   }

   /**
    * Method to obtain image URLs for a given JSoup Document and its source URL
    *
    * @param document
    *           Document object (JSoup Document)
    * @param sourceUrl
    *           String containing the source URL for the Document (used for relative image URLs)
    * @return List<String> containing Image URLs
    */
   private List<String> getImageUrls(final Document document, final String sourceUrl)
   {
      final List<String> allImageUrls = new ArrayList<>();

      if (document == null)
      {
         return allImageUrls;
      }

      final Elements anchorElements = document.getElementsByTag("a");
      final Elements imageElements = document.getElementsByTag("img");

      // Obtain all image references from <img> elements
      for (final Element element : imageElements)
      {
         final String imageUrl = element.attr("src");
         if (!imageUrl.isEmpty())
         {
            if (hasImageExtension(imageUrl))
            {
               if (!allImageUrls.contains(imageUrl))
               {
                  allImageUrls.add(imageUrl);
               }
            }
         }
      }

      // Obtain all image references from <a> elements
      for (final Element element : anchorElements)
      {
         final String anchorHref = element.attr("href");
         if (!anchorHref.isEmpty())
         {
            if (hasImageExtension(anchorHref))
            {
               allImageUrls.add(anchorHref);
            }
         }
      }

      final List<String> urls = new ArrayList<>();
      final List<File> imageFiles = new ArrayList<>();

      for (final String basicUrlString : allImageUrls)
      {

         try
         {
            String urlString = basicUrlString;

            if (basicUrlString.startsWith("//"))
            {
               urlString = "https:" + basicUrlString;
            }
            else
            {
               if (basicUrlString.startsWith("/"))
               {
                  final URL src = new URL(sourceUrl);
                  urlString = src.getProtocol() + "://" + src.getHost() + basicUrlString;
               }
            }

            if (urlString.startsWith("https://en.wikipedia.org/wiki/File:"))
            {
               continue;
            }

            final URL url = new URL(urlString);
            log.debug(urlString);

            File file = new File(directory.getAbsoluteFile() + File.separator + getFilename(urlString));
            if (file.exists())
            {
               file = new File(directory.getAbsolutePath() + File.separator + UUID.randomUUID().toString()
                     + getFilename(urlString));
            }

            FileUtils.copyURLToFile(url, file);

            // TODO Local copy of File is available for Computer Vision Processing if wanted

            // TODO should move the "file size filter" to properties
            if (file.length() > 20 * 1024)
            {
               if (!listContainsFile(file, imageFiles))
               {
                  imageFiles.add(file);
                  urls.add(urlString);
               }
               else
               {
                  file.delete();
               }
            }
            else
            {
               file.delete();
            }

         }
         catch (final MalformedURLException e)
         {
            log.error("Error creating URL object", e);
         }
         catch (final IOException e)
         {
            log.error("Error writing URL content to file", e);
         }
      }

      return urls;
   }

   /**
    * Method to detect if a filename has a supported image extention
    *
    * @param string
    *           String containing the filename
    * @return boolean true if filename has an image extension, false if not
    */
   private boolean hasImageExtension(final String string)
   {
      for (final String affix : imageExtensions)
      {
         if (string.toLowerCase().endsWith(affix))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Method to determine if a file listing already contains a given file
    *
    * @param file
    *           File object to test if we already contain within our list
    * @param fileList
    *           List<File> of already contained files
    * @return boolean true if list already contains the file, false if not.
    * @throws IOException
    *            Thrown if a file does not exist
    */
   private boolean listContainsFile(final File file, final List<File> fileList) throws IOException
   {
      for (final File containedFile : fileList)
      {
         if (Files.equal(file, containedFile))
         {
            return true;
         }
      }
      return false;
   }
}
