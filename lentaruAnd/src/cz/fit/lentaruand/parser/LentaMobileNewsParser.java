package cz.fit.lentaruand.parser;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import cz.fit.lentaruand.downloader.Page;
import cz.fit.lentaruand.parser.exceptions.MobileNewsParseException;

public class LentaMobileNewsParser implements NewsParser<MobileNews> {
	private static final Logger logger = Logger.getLogger(LentaMobileNewsParser.class.getName());
	
	private static final Pattern NEWS_IMAGE_CAPTION = Pattern.compile("<div class=\"b-label__caption\">(.+?)</div>");
	private static final int NEWS_IMAGE_CAPTION_GROUPS = 1;
	private static final Pattern NEWS_IMAGE_CREDITS = Pattern.compile("<div class=\"b-label__credits\">(.+?)</div>");
	private static final int NEWS_IMAGE_CREDITS_GROUPS = 1;
	private static final Pattern NEWS_BODY = Pattern.compile("<div class=\"b-topic__body\">(.+?)</div>");
	private static final int NEWS_BODY_GROUPS = 1;
	
	@Override
	public MobileNews parse(Page page) throws MobileNewsParseException {
		String imageCaption = null;
		String imageCredits = null;
		String text = null;
		
		Iterator<List<String>> it = ParseHelper.createParser(page.getText(), NEWS_IMAGE_CAPTION, NEWS_IMAGE_CAPTION_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			imageCaption = val.get(1);
		}

		it = ParseHelper.createParser(page.getText(), NEWS_IMAGE_CREDITS, NEWS_IMAGE_CREDITS_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			imageCredits = val.get(1);
		}

		it = ParseHelper.createParser(page.getText(), NEWS_BODY, NEWS_BODY_GROUPS).iterator();

		if (it.hasNext()) {
			List<String> val = it.next();
			text = val.get(1);
		} else {
			logger.log(Level.SEVERE, "Error parsing url='" + page.getUrl().toExternalForm() + "'");
			throw new MobileNewsParseException(page.getUrl(), NEWS_BODY.pattern());
		}
		
		return new MobileNews(imageCaption, imageCredits, text);
	}
}