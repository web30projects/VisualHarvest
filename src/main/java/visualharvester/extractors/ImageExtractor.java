package visualharvester.extractors;

import java.io.File;
import java.io.IOException;
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

public class ImageExtractor {

	Logger log = Logger.getLogger(getClass());

	File directory;

	private final String[] imageExtensions = { ".jpg", ".jpeg", ".png", ".bmp", ".gif" };

	public ImageExtractor(String storageDirectoryPath) {
		// log.debug("Image Extractor storing local image files to " +
		// storageDirectoryPath);
		directory = new File(storageDirectoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public List<String> extractImageUrls(String sourceUrl) {
		// log.debug("Extracting Image URLs from " + sourceUrl);
		List<String> urls = new ArrayList<>();
		try {
			urls = getImageUrls(getDocument(sourceUrl));
		} catch (final IOException e) {
			// log.error("Error Extracting Image URLs from " + sourceUrl, e);
		}
		return urls;
	}

	private void filterImage(String urlString, List<String> urls) throws IOException {
		final URL url = new URL(urlString);

		// log.debug("URL: " + url.toString());

		File file = new File(directory.getAbsoluteFile() + File.separator + getFilename(urlString));
		if (file.exists()) {
			file = new File(directory.getAbsolutePath() + File.separator + UUID.randomUUID().toString()
					+ getFilename(urlString));
		}

		if (file.getName().equals("File")) {
			log.debug("XXXXXXXXXXXXXXX" + file.getName());
		}

		FileUtils.copyURLToFile(url, file);
		log.debug(file.getName());

		// If image file is greater than 15KB, consider it relevant
		// TODO include nudity filtering here
		if (file.length() > (25 * 1024)) {
			urls.add(urlString);
		} else {
			file.delete();
		}
	}

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

	private String getFilename(String urlFilePath) {
		String filename = urlFilePath.substring(urlFilePath.lastIndexOf("/") + 1);

		if (filename.contains(":")) {
			filename = filename.substring(filename.lastIndexOf(":") + 1);
		}

		return filename;
	}

	private List<String> getImageUrls(Document document) {
		final List<String> allImageUrls = new ArrayList<>();

		if (document == null) {
			return allImageUrls;
		}

		final Elements anchorElements = document.getElementsByTag("a");
		final Elements imageElements = document.getElementsByTag("img");

		// Obtain all image references from <img> elements
		for (final Element element : imageElements) {
			final String imageUrl = element.attr("src");
			if (!imageUrl.isEmpty()) {
				if (hasImageExtension(imageUrl)) {
					if (!allImageUrls.contains(imageUrl)) {
						allImageUrls.add(imageUrl);
					}
				}
			}
		}

		// Obtain all image references from <a> elements
		for (final Element element : anchorElements) {
			final String anchorHref = element.attr("href");
			if (!anchorHref.isEmpty()) {
				if (hasImageExtension(anchorHref)) {
					allImageUrls.add(anchorHref);
				}
			}
		}

		final List<String> urls = new ArrayList<>();

		// Filter images for size
		for (final String urlString : allImageUrls) {
			try {
				if (urlString.startsWith("http")) {
					filterImage(urlString, urls);
				}
				if (urlString.startsWith("//")) {
					filterImage("https:" + urlString, urls);
				}
			} catch (final MalformedURLException e) {
				log.error("Error creating URL object", e);
			} catch (final IOException e) {
				log.error("Error writing URL content to file", e);
			}
		}

		return urls;
	}

	private boolean hasImageExtension(String string) {
		for (final String affix : imageExtensions) {
			if (string.toLowerCase().endsWith(affix)) {
				return true;
			}
		}
		return false;
	}
}
