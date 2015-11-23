package visualharvester.extractors;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;

public class UrlExtractor {

	private final static String[] schemes = { "http", "https" };
	private final static UrlValidator validator = new UrlValidator(schemes, UrlValidator.ALLOW_LOCAL_URLS);
	Logger log = Logger.getLogger(getClass());

	public String extractUrl(String text) {

		final int indexOf = text.indexOf("http");
		String url = null;

		if (indexOf >= 0) {
			final String httpStart = text.substring(indexOf);

			int whitespace;
			boolean isInternal = false;

			for (whitespace = 0; whitespace < httpStart.length(); whitespace++) {
				if (Character.isWhitespace(httpStart.charAt(whitespace))) {
					isInternal = true;
					break;
				}
			}

			if (!isInternal) {
				url = httpStart;
			} else {
				url = httpStart.substring(0, whitespace);
			}
		} else {
			url = "";
		}

		final String extractedUrl = validateUrl(url);
		// log.debug("URL: " + extractedUrl);
		return extractedUrl;
	}

	private String validateUrl(String url) {
		if (url.endsWith(".")) {
			url = url.substring(0, url.lastIndexOf("."));
		}
		if (url.endsWith("?")) {
			url = url.substring(0, url.lastIndexOf("?"));
		}
		if (url.endsWith("!")) {
			url = url.substring(0, url.lastIndexOf("!"));
		}

		if (validator.isValid(url)) {
			return url;
		}
		return "";
	}

}
