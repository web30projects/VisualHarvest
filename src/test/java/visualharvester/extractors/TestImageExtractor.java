package visualharvester.extractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

public class TestImageExtractor {

	private static final String testUrl = "https://en.wikipedia.org/wiki/Binary_tree";
	static File directory;

	@AfterClass
	public static void afterClass() {
		deleteDirectory(directory);
	}

	@BeforeClass
	public static void beforeClass() {
		directory = Files.createTempDir();
	}

	private static void deleteDirectory(File directory) {
		for (final File file : directory.listFiles()) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				file.delete();
			}
		}
		directory.delete();
	}

	Logger log = Logger.getLogger(getClass());

	@Test
	public void testExtractImageUrls() {
		log.debug("testExtractImageUrls");

		final ImageExtractor imageExtractor = new ImageExtractor(directory.getAbsolutePath());
		final List<String> extractedUrls = imageExtractor.extractImageUrls(testUrl);

		assertTrue(extractedUrls.contains("https://en.wikipedia.org/wiki/File:Waldburg_Ahnentafel.jpg"));
		assertEquals(1, extractedUrls.size());
	}

}
