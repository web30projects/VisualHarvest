package visualharvester.extractors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.resolver.ResolvedLocation;

/*
 * Class for extracting Geospatial Entities from unstructured text
 */
public class EntityExtractor {

	private final Logger log = Logger.getLogger(getClass());
	private final GeoParser parser;

	/*
	 * Constructor Takes a Clavin GeoParser object as an
	 * input
	 */
	public EntityExtractor(GeoParser parser) {
		this.parser = parser;
	}

	/*
	 * Entity Extraction Method
	 */
	public List<ResolvedLocation> extractEntities(String text) {
		final List<ResolvedLocation> entities = new ArrayList<>();

		try {
			final List<ResolvedLocation> parse = parser.parse(text);
			entities.addAll(parse);
		} catch (final Exception e) {
			log.error("Error parsing geospatial entities", e);
		}

		return entities;
	}

}
