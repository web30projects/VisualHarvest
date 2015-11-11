package visualharvester.extractors;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

	public ImageExtractor(String storageDirectoryPath, String subdirectory) {
		directory = new File(storageDirectoryPath + File.separator + subdirectory);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public List<String> extractImageUrls(String sourceUrl) {
		log.debug("Extracting Image URLs from " + sourceUrl);
		List<String> urls = new ArrayList<>();
		try {
			urls = getImageUrls(getDocument(sourceUrl));
		} catch (final IOException e) {
			log.error("Error Extracting Image URLs from " + sourceUrl, e);
		}
		return urls;
	}

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).get();
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
					allImageUrls.add(imageUrl);
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
					final URL url = new URL(urlString);
					final File file = new File(url.getFile());
					FileUtils.copyURLToFile(url, file);

					// If image file is greater than 15KB, consider it relevant
					// TODO include nudity filtering here
					if (file.length() > (15 * 1024)) {
						log.debug("saving file");
						File image = new File(directory.getAbsolutePath() + File.separator + file.getName());
						FileUtils.copyFile(file, image);

						urls.add(urlString);
					}

				}

				if (urlString.startsWith("//")) {
					final URL url = new URL("https:" + urlString);
					final File file = new File(url.getFile());
					FileUtils.copyURLToFile(url, file);

					// If image file is greater than 15KB, consider it relevant
					// TODO include nudity filtering here
					if (file.length() > (5 * 1024)) {
						log.debug("saving file");
						File image = new File(directory.getAbsolutePath() + File.separator + file.getName());
						FileUtils.copyFile(file, image);

						urls.add("https:" + urlString);
					}

				}

			} catch (final MalformedURLException e) {
				log.error("Error creating URL object", e);
			} catch (final IOException e) {
				log.error("Error writing URL content to file", e);
			}
		}

		if (urls.isEmpty()) {
			deleteDirectory(directory);
		}

		return urls;
	}

	private void deleteDirectory(File directory) {
		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				deleteDirectory(file);
			} else {
				file.delete();
			}
		}
		directory.delete();
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
