package visualharvester.extractors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.resolver.ResolvedLocation;

/**
 * Geospatial Entity Extractor class
 */
public class EntityExtractor
{

   /** The Logger */
   private final Logger log = Logger.getLogger(getClass());

   /** Clavin GeoParser object */
   private final GeoParser parser;

   /**
    * Entity Extractor Constructor
    *
    * @param parser
    *           GeoParser object
    */
   public EntityExtractor(final GeoParser parser)
   {
      this.parser = parser;
   }

   /**
    * Entity Extractor's process method: Parses the input text and produces a listing of ResolvedLocation (Clavin
    * Library class) objects
    *
    * @param text
    *           String of the text to be parsed for geospatial entities
    * @return List<ResolvedLocation>
    */
   public List<ResolvedLocation> extractEntities(final String text)
   {
      final List<ResolvedLocation> entities = new ArrayList<>();

      try
      {
         final List<ResolvedLocation> parse = parser.parse(text);
         entities.addAll(parse);
      }
      catch (final Exception e)
      {
         log.error("Error parsing geospatial entities", e);
      }

      return entities;
   }

}
