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

/*
 * Image Extraction class
 */
public class ImageExtractor {

	Logger log = Logger.getLogger(getClass());

	File directory;

	// TODO this list should be moved into a configuration
	// file
	// Currently supported Image file types
	private final String[] imageExtensions = { ".jpg", ".jpeg", ".png",
			".bmp" };

	/*
	 * Constructor Takes a String containing a path where
	 * images can be stashed upon download
	 */
	public ImageExtractor(String storageDirectoryPath) {
		directory = new File(storageDirectoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}
	}

	public List<String> extractImageUrls(String sourceUrl) {
		List<String> urls = new ArrayList<>();
		try {
			final Document document = getDocument(sourceUrl);
			urls = getImageUrls(document, getFinalURL(sourceUrl));

		} catch (final IOException e) {
			log.error("Error Extracting Image URLs from " + sourceUrl, e);
		}
		return urls;
	}

	private Document getDocument(String url) throws IOException {
		return Jsoup.connect(url).get();
	}

	private String getFilename(String urlFilePath) {
		String filename = urlFilePath
				.substring(urlFilePath.lastIndexOf("/") + 1);
		if (filename.contains(":")) {
			filename = filename.substring(filename.lastIndexOf(":") + 1);
		}
		return filename;
	}

	/*
	 * Method for Resolving URLs
	 */
	public String getFinalURL(String url) {
		try {
			final HttpURLConnection con = (HttpURLConnection) new URL(url)
					.openConnection();
			con.setInstanceFollowRedirects(false);
			con.connect();
			con.getInputStream();

			if ((con.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM)
					|| (con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP)) {
				final String redirectUrl = con.getHeaderField("Location");
				return getFinalURL(redirectUrl);
			}

		} catch (final IOException e) {
			log.error("Error obtaining redirected URL", e);
		}
		return url;
	}

	private List<String> getImageUrls(Document document, String sourceUrl) {
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
		final List<File> imageFiles = new ArrayList<>();

		for (final String basicUrlString : allImageUrls) {

			try {
				String urlString = basicUrlString;

				if (basicUrlString.startsWith("//")) {
					urlString = "https:" + basicUrlString;
				} else {
					if (basicUrlString.startsWith("/")) {
						final URL src = new URL(sourceUrl);
						urlString = src.getProtocol() + "://" + src.getHost()
								+ basicUrlString;
					}
				}

				if (urlString
						.startsWith("https://en.wikipedia.org/wiki/File:")) {
					continue;
				}

				final URL url = new URL(urlString);

				File file = new File(directory.getAbsoluteFile()
						+ File.separator + getFilename(urlString));
				if (file.exists()) {
					file = new File(directory.getAbsolutePath() + File.separator
							+ UUID.randomUUID().toString()
							+ getFilename(urlString));
				}

				FileUtils.copyURLToFile(url, file);

				// If image file is greater than 15KB,
				// consider it relevant
				// TODO include nudity filtering here
				if (file.length() > (35 * 1024)) {
					if (!listContainsFile(file, imageFiles)) {
						imageFiles.add(file);
						urls.add(urlString);
					} else {
						file.delete();
					}
				} else {
					file.delete();
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

	private boolean listContainsFile(File file, List<File> fileList)
			throws IOException {
		for (final File containedFile : fileList) {
			if (Files.equal(file, containedFile)) {
				return true;
			}
		}
		return false;
	}
}
