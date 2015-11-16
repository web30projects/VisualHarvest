package visualharvester.extractors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.bericotech.clavin.GeoParser;
import com.bericotech.clavin.resolver.ResolvedLocation;

public class EntityExtractor {
	private final Logger log = Logger.getLogger(getClass());
	private final GeoParser parser;

	public EntityExtractor(GeoParser parser) {
		this.parser = parser;
	}

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
