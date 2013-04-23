package cz.fit.lentaruand.parser.rss;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.downloader.Page;

/**
 * LentaRssParser is used for parsing RSS page from Lenta.ru site. It could be
 * any type of the news: news, photo, article, etc.
 * 
 * @author kacpa01
 * 
 */
public class LentaRssParser {
	private final static String ITEM_PATH = "/rss/channel/item";
	private final static String GUID = "guid";
	private final static String TITLE = "title";
	private final static String LINK = "link";
	private final static String AUTHOR = "author";
	private final static String DESCRIPTION = "description";
	private final static String PUBDATE = "pubDate";
	private final static String IMAGEURL = "enclosure/@url";
	
	private final XPathExpression items;
	private final XPathExpression guid;
	private final XPathExpression title;
	private final XPathExpression link;
	private final XPathExpression author;
	private final XPathExpression description;
	private final XPathExpression pubDate;
	private final XPathExpression imageUrl;

	private final String datePattern = "EEE, dd MMM yyyy HH:mm:ss Z";
	private final SimpleDateFormat lentaDateSDF = new SimpleDateFormat(datePattern);
	
	/**
	 * Default constructor. Instantiates all internal XPath related objects.
	 */
	public LentaRssParser() {
		XPath xp = XPathFactory.newInstance().newXPath();
		try {
			items = xp.compile(ITEM_PATH);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + ITEM_PATH);
		}

		try {
			guid =  xp.compile(GUID);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + GUID);
		}
		
		try {
			title = xp.compile(TITLE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + TITLE);
		}
		
		try {
			link = xp.compile(LINK);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + LINK);
		}
		
		try {
			author = xp.compile(AUTHOR);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + AUTHOR);
		}
		
		try {
			description = xp.compile(DESCRIPTION);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + DESCRIPTION);
		}
		
		try {
			pubDate = xp.compile(PUBDATE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + PUBDATE);
		}
		
		try {
			imageUrl = xp.compile(IMAGEURL);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			throw new RuntimeException("Error compiling XPath expression: " + IMAGEURL);
		}
	}
	
	/**
	 * Read and parse all items from RSS page.
	 * 
	 * @param page
	 *            is the page to parse.
	 * @param rubric
	 *            is the value from Rubrics enum. Specifies to which rubric
	 *            parsed page is related.
	 * @param newsType
	 *            is the value from NewsType enum. Specifies the type of the
	 *            parsed news.
	 * @return Collection of the parsed LentaRssItem objects. Some fields in
	 *         that objects can be null. For example if Article RSS is parsed,
	 *         then Author field is set, but for News RSS Author field will be
	 *         null.
	 * @throws XPathExpressionException if some error occurred during parsing with XPath.
	 */
	public Collection<LentaRssItem> parseItems(Page page, Rubrics rubric, NewsType newsType) throws XPathExpressionException {
		StringReader sr = new StringReader(page.getText());
		List<LentaRssItem> resultItems = new ArrayList<LentaRssItem>();
		
		try {
			NodeList itemNodes = (NodeList) items.evaluate(new InputSource(sr), XPathConstants.NODESET);
			
			for (int i = 0; i < itemNodes.getLength(); ++i) {
				Node itemNode = itemNodes.item(i);
				
				String guidStr = (String) guid.evaluate(itemNode, XPathConstants.STRING);
				String titleStr = (String) title.evaluate(itemNode, XPathConstants.STRING);
				String linkStr = (String) link.evaluate(itemNode, XPathConstants.STRING);
				String authorStr = (String) author.evaluate(itemNode, XPathConstants.STRING);
				String descriptionStr = (String) description.evaluate(itemNode, XPathConstants.STRING);
				String pubDateStr = (String) pubDate.evaluate(itemNode, XPathConstants.STRING);
				String imageUrlStr = (String) imageUrl.evaluate(itemNode, XPathConstants.STRING);
				
				Date date; 
				try {
					date = lentaDateSDF.parse(pubDateStr);
				} catch (ParseException e) {
					e.printStackTrace();
					continue;
				}
				
				resultItems.add(new LentaRssItem(guidStr, newsType, titleStr, linkStr, authorStr, descriptionStr, date, imageUrlStr, rubric));
			}
			
			return resultItems;
		} finally {
			sr.close();
		}
	}
}
