package com.xeppaka.lentareader.parser.rss;

import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;

import java.util.Date;

/**
 * LentaRssItem is the data holder for the parsed RSS page.
 * 
 * @author kacpa01
 * 
 */
public class LentaRssItem {
	private String guid;
	private NewsType type;
	private String title;
	private String link;
	private String author;
	private String description;
	private Date pubDate;
	private String imageLink;
	private Rubrics rubric;

	/**
	 * Parameterized constructor. Initializes all fields except rubric and news
	 * type. Rubric and News type can be set later.
	 * 
	 * @param guid must not be null or empty.
	 * @param title must not be null or empty.
	 * @param link must not be null or empty.
	 * @param author can be null or empty.
	 * @param description must not be null or empty.
	 * @param pubDate must not be null.
	 * @param imageLink can be null or empty.
	 */
	public LentaRssItem(String guid, String title, String link, String author,
			String description, Date pubDate, String imageLink) {
		this(guid, null, title, link, author, description, pubDate, imageLink, null);
	}

	/**
	 * Parameterized constructor. Initializes all fields.
	 * 
	 * @param guid must not be null or empty.
	 * @param type can be null.
	 * @param title must not be null or empty.
	 * @param link must not be null or empty.
	 * @param author can be null or empty.
	 * @param description must not be null or empty.
	 * @param pubDate must not be null.
	 * @param imageLink can be null or empty.
	 * @param rubric can be null or empty.
	 */
	public LentaRssItem(String guid, NewsType type, String title, String link, String author,
			String description, Date pubDate, String imageLink, Rubrics rubric) {
		if (guid == null || (guid.length()==0))
			throw new IllegalArgumentException("Argument guid must not be null or empty");
		
		if (title == null || (title.length()==0))
			throw new IllegalArgumentException("Argument title must not be null or empty");
		
		if (link == null || (link.length()==0))
			throw new IllegalArgumentException("Argument link must not be null or empty");
		
		if (description == null || (description.length()==0))
			throw new IllegalArgumentException("Argument description must not be null or empty");
		
		if (pubDate == null)
			throw new IllegalArgumentException("Argument pubDate must not be null");
		
		this.guid = guid;
		this.type = type;
		this.title = title;
		this.link = link;
		this.author = author;
		this.description = description;
		this.pubDate = pubDate;
		this.imageLink = imageLink;
		this.rubric = rubric;
	}

	public String getGuid() {
		return guid;
	}
	
	public NewsType getType() {
		return type;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getLink() {
		return link;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Date getPubDate() {
		return pubDate;
	}
	
	public String getImageLink() {
		return imageLink;
	}
	
	public Rubrics getRubric() {
		return rubric;
	}
}
