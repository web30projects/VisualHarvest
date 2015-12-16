package visualharvester.extractors;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;

/**
 * Class for Extracting URLs from Text
 */
public class UrlExtractor
{

   /** Array of supported schemes */
   private final static String[] schemes =
   { "http", "https" };
   /** The Apache Commons URL Validator */
   private final static UrlValidator validator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
   /** The Logger */
   Logger log = Logger.getLogger(getClass());

   /**
    * Method for extracting a URL from a String containing text
    *
    * @param text
    *           String containing text
    * @return String containing a URL from within the text, if found. returns null if none are found.
    */
   public String extractUrl(final String text)
   {

      final int indexOf = text.indexOf("http");
      String url = null;

      if (indexOf >= 0)
      {
         final String httpStart = text.substring(indexOf);

         int whitespace;
         boolean isInternal = false;

         for (whitespace = 0; whitespace < httpStart.length(); whitespace++)
         {
            if (Character.isWhitespace(httpStart.charAt(whitespace)))
            {
               isInternal = true;
               break;
            }
         }

         if (!isInternal)
         {
            url = httpStart;
         }
         else
         {
            url = httpStart.substring(0, whitespace);
         }
      }
      else
      {
         url = "";
      }

      final String extractedUrl = validateUrl(url);
      return extractedUrl;
   }

   /**
    * Method to validate a URL contained within a String Also removes trailing english punctuation if the URL is
    * included within a sentence.
    *
    * @param url
    *           String the URL
    * @return String the URL sans-punctuation an empty string if deemed invalid.
    */
   private String validateUrl(final String url)
   {
      String removePeriods = url;
      if (url.endsWith("."))
      {
         removePeriods = url.substring(0, url.lastIndexOf("."));
      }

      String removeQuestion = removePeriods;
      if (url.endsWith("?"))
      {
         removeQuestion = url.substring(0, url.lastIndexOf("?"));
      }

      String removeExclamations = removeQuestion;
      if (url.endsWith("!"))
      {
         removeExclamations = url.substring(0, url.lastIndexOf("!"));
      }

      if (validator.isValid(removeExclamations))
      {
         return removeExclamations;
      }
      return "";
   }

}
