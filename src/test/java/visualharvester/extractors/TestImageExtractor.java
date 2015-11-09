package visualharvester.extractors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

public class TestImageExtractor {

	private static final String testUrl = "https://en.wikipedia.org/wiki/Binary_tree";
	Logger log = Logger.getLogger(getClass());

	@Test
	public void testExtractImageUrls() {
		log.debug("testExtractImageUrls");

		final ImageExtractor imageExtractor = new ImageExtractor();
		final List<String> extractedUrls = imageExtractor.extractImageUrls(testUrl);

		assertTrue(extractedUrls.contains(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/f/f7/Binary_tree.svg/192px-Binary_tree.svg.png"));
		assertTrue(extractedUrls.contains(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/2/26/Waldburg_Ahnentafel.jpg/220px-Waldburg_Ahnentafel.jpg"));
		assertTrue(extractedUrls.contains(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/8/86/Binary_tree_in_array.svg/300px-Binary_tree_in_array.svg.png"));
		assertTrue(extractedUrls.contains(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/c/cd/N-ary_to_binary.svg/400px-N-ary_to_binary.svg.png"));
		assertTrue(extractedUrls.contains(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/4/43/BinaryTreeRotations.svg/300px-BinaryTreeRotations.svg.png"));
		assertTrue(extractedUrls.contains(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/9/95/Insertion_of_binary_tree_node.svg/360px-Insertion_of_binary_tree_node.svg.png"));
		assertTrue(extractedUrls.contains(
				"https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Deletion_of_internal_binary_tree_node.svg/360px-Deletion_of_internal_binary_tree_node.svg.png"));
		assertEquals(7, extractedUrls.size());
	}

}
