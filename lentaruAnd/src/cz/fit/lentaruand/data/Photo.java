package cz.fit.lentaruand.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;

import cz.fit.lentaruand.parser.rss.LentaRssItem;

public class Photo extends NewsObject {

	private String secondTitle;
	private String description;
	private Collection<PhotoImage> photos;
	
	public Photo(String guid, String title, String secondTitle,
			String description, Collection<PhotoImage> photos, String link,
			Date pubDate, Rubrics rubric, boolean rubricUpdateNeed) {
		super(guid, title, link, pubDate, rubric, rubricUpdateNeed);
		setSecondTitle(secondTitle);
		setDescription(description);
		setPhotos(photos);
	}
	
	public Photo(LentaRssItem rssItem) {
		super(rssItem);
		setDescription(rssItem.getDescription());
		photos = Collections.emptyList();
	}

	public String getSecondTitle() {
		return secondTitle;
	}

	public void setSecondTitle(String secondTitle) {
		this.secondTitle = secondTitle;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<PhotoImage> getPhotos() {
		return photos;
	}

	public void setPhotos(Collection<PhotoImage> photos) {
		if (photos == null)
			throw new IllegalArgumentException("Argument photos must not be null.");
			
		this.photos = photos;
	}

	@Override
	public NewsType getType() {
		return NewsType.PHOTO;
	}
}