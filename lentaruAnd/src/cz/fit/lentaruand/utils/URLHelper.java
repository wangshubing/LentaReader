package cz.fit.lentaruand.utils;

import java.net.MalformedURLException;

import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;

/**
 * URLHelper helps to get URLs from which news can be downloaded (e.g. get URL
 * for some rubric or get URL to mobile version of article from URL to full
 * version of article.
 * 
 * @author kacpa01
 * 
 */
public class URLHelper {
	
	/**
	 * Returns URL to RSS page for specified rubric and news type.
	 * 
	 * @param rubric
	 *            is the value from Rubrics enum that specifies news rubric.
	 * @param type
	 *            is the value from NewsType enum.
	 * @return URL that can be used to download RSS page.
	 */
	public static String getRssForRubric(Rubrics rubric, NewsType type) {
		if (rubric == null)
			throw new IllegalArgumentException("Argument rubric must not be null");
		
		return LentaConstants.URL_ROOT + rubric.getRssPath(type);
	}

	/**
	 * Returns URL to mobile version of the site created from the link to the
	 * full version.
	 * 
	 * @param link
	 *            is the link to the full version of the site (e.g.
	 *            http://lenta.ru/news... )
	 * @return URL that can be used to download mobile page.
	 */
	public static String createMobileUrl(String link) {
		String fullUrl = link.replaceFirst("http://", "http://m.");
		
		return fullUrl;
	}
	
	/**
	 * Returns id for the news image parsed from image URL.
	 * E.g. url looks like http://icdn.lenta.ru/images/2013/07/20/13/20130720130546191/pic_529a4d2f9d668a24711cd4f11d31e32a.jpg,
	 * than returned id will be pic_529a4d2f9d668a24711cd4f11d31e32a.
	 * 
	 * @param newsImageUrl is the url of the image. 
	 * @return Parsed id of the image.
	 */
	public static String getImageId(String newsImageUrl) throws MalformedURLException {
		if (newsImageUrl == null || newsImageUrl.isEmpty()) {
			throw new IllegalArgumentException("newsImageUrl is null or empty");
		}
		
		int lastSlash = newsImageUrl.lastIndexOf('/');
		
		if (lastSlash == -1) {		
			throw new MalformedURLException("Image url has wrong format: " + newsImageUrl);
		}
		
		int lastDot = newsImageUrl.lastIndexOf('.');
		if (lastDot == -1)
			lastDot = newsImageUrl.length();
		
		if (lastDot >= lastSlash) {
			throw new MalformedURLException("Image url has wrong format: " + newsImageUrl);
		}
		
		return newsImageUrl.substring(lastSlash, lastDot);
	}
}