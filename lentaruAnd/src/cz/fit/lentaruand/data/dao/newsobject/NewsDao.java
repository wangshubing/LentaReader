package cz.fit.lentaruand.data.dao.newsobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import cz.fit.lentaruand.data.Link;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.Dao;
import cz.fit.lentaruand.data.dao.NewsLinksDao;
import cz.fit.lentaruand.data.dao.async.AsyncCachedNewsObjectDao;
import cz.fit.lentaruand.data.dao.async.AsyncNewsObjectDao;
import cz.fit.lentaruand.data.dao.sync.SynchronizedNewsObjectDao;
import cz.fit.lentaruand.data.db.NewsEntry;
import cz.fit.lentaruand.data.db.SQLiteType;
import cz.fit.lentaruand.data.provider.LentaProvider;
import cz.fit.lentaruand.utils.LentaConstants;

public final class NewsDao {
	private static final int CACHE_MAX_OBJECTS = LentaConstants.DAO_CACHE_MAX_OBJECTS;
	
	private static final LruCache<Long, News> cacheId = new LruCache<Long, News>(CACHE_MAX_OBJECTS);
	
	private static final Object sync = new Object();
	
	public final static AsyncNewsObjectDao<News> getInstance(ContentResolver contentResolver) {
		if (contentResolver == null) {
			throw new IllegalArgumentException("contentResolver is null.");
		}
		
		return new SynchronizedNewsObjectDao<News>(new AsyncCachedNewsObjectDao<News>(new ContentResolverNewsDao(contentResolver), cacheId), sync);
	}

	private NewsDao() {
	}
	
	private static class ContentResolverNewsDao extends ContentResolverNewsObjectDao<News> {
		private static final String[] projectionAll = {
			NewsEntry._ID,
			NewsEntry.COLUMN_NAME_GUID,
			NewsEntry.COLUMN_NAME_TITLE,
			NewsEntry.COLUMN_NAME_LINK,
			NewsEntry.COLUMN_NAME_IMAGELINK,
			NewsEntry.COLUMN_NAME_IMAGECAPTION,
			NewsEntry.COLUMN_NAME_IMAGECREDITS,
			NewsEntry.COLUMN_NAME_PUBDATE,
			NewsEntry.COLUMN_NAME_RUBRIC,
			NewsEntry.COLUMN_NAME_RUBRIC_UPDATE,
			NewsEntry.COLUMN_NAME_BRIEFTEXT,
			NewsEntry.COLUMN_NAME_FULLTEXT
		};
		
		private final Dao<Link> newsLinksDao;
		private final ImageDao imageDao;
		
		public ContentResolverNewsDao(ContentResolver cr) {
			super(cr);
			newsLinksDao = NewsLinksDao.getInstance(cr);
			imageDao = ImageDao.getInstance(cr);
		}

		@Override
		protected ContentValues prepareContentValues(News news) {
			ContentValues values = new ContentValues();
			
			values.put(NewsEntry.COLUMN_NAME_GUID, news.getGuid());
			values.put(NewsEntry.COLUMN_NAME_TITLE, news.getTitle());
			values.put(NewsEntry.COLUMN_NAME_LINK, news.getLink());
			
			if (news.getImageLink() == null)
				values.putNull(NewsEntry.COLUMN_NAME_IMAGELINK);
			else
				values.put(NewsEntry.COLUMN_NAME_IMAGELINK, news.getImageLink());
			
			if (news.getImageCaption() == null)
				values.putNull(NewsEntry.COLUMN_NAME_IMAGECAPTION);
			else
				values.put(NewsEntry.COLUMN_NAME_IMAGECAPTION, news.getImageCaption());
			
			if (news.getImageCredits() == null)
				values.putNull(NewsEntry.COLUMN_NAME_IMAGECREDITS);
			else
				values.put(NewsEntry.COLUMN_NAME_IMAGECREDITS, news.getImageCredits());
			
			values.put(NewsEntry.COLUMN_NAME_PUBDATE, news.getPubDate().getTime());
			values.put(NewsEntry.COLUMN_NAME_RUBRIC, news.getRubric().name());
			values.put(NewsEntry.COLUMN_NAME_BRIEFTEXT, news.getBriefText());
			values.put(NewsEntry.COLUMN_NAME_RUBRIC_UPDATE, news.isRubricUpdateNeed() ? 1 : 0);
			
			if (news.getFullText() == null)
				values.putNull(NewsEntry.COLUMN_NAME_FULLTEXT);
			else
				values.put(NewsEntry.COLUMN_NAME_FULLTEXT, news.getFullText());
			
			return values;
		}
	
		@Override
		protected News createDataObject(Cursor cur) {
			long id = cur.getLong(cur.getColumnIndexOrThrow(NewsEntry._ID));
			String guidDb = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_GUID));
			String title = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_TITLE));
			String link = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_LINK));
			String imageLink = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGELINK));
			String imageCaption = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGECAPTION));
			String imageCredits = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_IMAGECREDITS));
			Date pubDate = new Date(cur.getLong(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_PUBDATE)));
			Rubrics rubric = Rubrics.valueOf(cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_RUBRIC)));
			boolean rubricUpdateNeed = cur.getInt(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_RUBRIC_UPDATE)) > 0;
			
			String briefText = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_BRIEFTEXT));
			String fullText = cur.getString(cur.getColumnIndexOrThrow(NewsEntry.COLUMN_NAME_FULLTEXT));
			
			return new News(id, guidDb, title, link, briefText, fullText, pubDate, imageLink, imageCaption, imageCredits, null, rubric, rubricUpdateNeed);
		}
	
		@Override
		protected Uri getContentProviderUri() {
			return LentaProvider.CONTENT_URI_NEWS;
		}
	
		@Override
		protected String getIdColumnName() {
			return NewsEntry._ID;
		}
	
		@Override
		protected String getKeyColumnName() {
			return NewsEntry.COLUMN_NAME_GUID;
		}
		
		@Override
		protected SQLiteType getKeyColumnType() {
			return SQLiteType.TEXT;
		}
	
		@Override
		protected String[] getProjectionAll() {
			return projectionAll;
		}

		@Override
		protected String getRubricColumnName() {
			return NewsEntry.COLUMN_NAME_RUBRIC;
		}

		@Override
		public List<News> read() {
			List<News> news;
			
			synchronized(sync) {
				news = super.read();
				
				for (News n : news) {
					readOtherNewsParts(n);
				}
			}
				
			Collections.sort(news);
			
			return news;
		}

		@Override
		public News read(long id) {
			synchronized(sync) {
				News news = super.read(id);
				
				if (news == null)
					return news;
		
				readOtherNewsParts(news);
				
				return news;
			}
		}
	
		@Override
		public News read(String key) {
			synchronized(sync) {
				News news = super.read(key);
				
				if (news == null)
					return news;
				
				readOtherNewsParts(news);
				
				return news;
			}
		}
	
		@Override
		public News read(SQLiteType keyType,
				String keyColumnName, String keyValue) {
			synchronized(sync) {
				News news = super.read(keyType, keyColumnName, keyValue);
				
				if (news == null)
					return news;
		
				readOtherNewsParts(news);
				
				return news;
			}
		}
	
		@Override
		public long create(News news) {
			synchronized(sync) {
				long newsId = super.create(news);
		
				createNewsLinks(news);
				
				return newsId;
			}
		}
		
		@Override
		public Collection<Long> create(Collection<News> dataObjects) {
			synchronized(sync) {
				Collection<Long> ids = super.create(dataObjects);
				
				for (News n : dataObjects) {
					createNewsLinks(n);
				}
				
				return ids;
			}
		}

		private void createNewsLinks(News news) {
			Collection<Link> links = news.getLinks();
			
			for (Link link : links) {
				link.setNewsId(news.getId());
			}
			
			newsLinksDao.create(links);
		}
		
		@Override
		public int update(News news) {
			synchronized(sync) {
				int result = super.update(news);
				
				updateOtherNewsParts(news);
				
				return result;
			}
		}
		
		@Override
		public Collection<News> readForParentObject(long parentId) {
			return null;
		}

		private void updateOtherNewsParts(News news) {
			Collection<Link> links = news.getLinks();
			
			for (Link link : links) {
				newsLinksDao.update(link);
			}
			
			BitmapReference imageRef = news.getImage();
			Bitmap image = imageRef.getBitmap();
			
			if (image != null && image != ImageDao.getNotAvailableImage().getBitmap()) {
				try {
					String imageLink = news.getImageLink();
					
					if (imageLink != null) {
						BitmapReference newImageRef = imageDao.create(imageLink, image);
						BitmapReference newThumbnailImageRef = imageDao.readThumbnail(imageLink);
						
						news.setImage(newImageRef);
						news.setThumbnailImage(newThumbnailImageRef);
					}
				} finally {
					imageRef.releaseBitmap();
				}
			}
		}

		private void readOtherNewsParts(News news) {
			// read news links
			List<Link> links = new ArrayList<Link>(newsLinksDao.readForParentObject(news.getId()));
			Collections.sort(links);
			
			news.setLinks(links);
			
			String imageLink = news.getImageLink();
			if (imageLink != null && !TextUtils.isEmpty(imageLink)) {
				news.setImage(imageDao.read(imageLink));
				news.setThumbnailImage(imageDao.readThumbnail(imageLink));
			} else {
				news.setImage(ImageDao.getNotAvailableImage());
				news.setThumbnailImage(ImageDao.getNotAvailableImage());
			}
		}
	}
}