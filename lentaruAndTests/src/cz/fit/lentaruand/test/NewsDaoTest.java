package cz.fit.lentaruand.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import cz.fit.lentaruand.data.Link;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.data.dao.NewsLinksDao;
import cz.fit.lentaruand.data.db.LentaDbHelper;

public class NewsDaoTest extends AndroidTestCase {

	private LentaDbHelper dbHelper;
	private Context context;
	private News testNews;
	private Date date;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		context = getContext();
		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
		date = new Date();
		Link l1 = new Link("http://www.sky.com", "Link to the heaven", date);
		Link l2 = new Link("http://www.sky1.com", "Link to the heaven 1", date);
		Link l3 = new Link("http://www.sky2.com", "Link to the heaven 2", date);
		testNews = new News("guid1", "News 1", "http://www.1.ru", "Brief news info", "Full news text", 
				date, "http://www.image.com/image.png", "Image caption", "Photo: PK", Arrays.asList(l1, l2, l3), Rubrics.CULTURE, true);
		dbHelper = new LentaDbHelper(context);
	}

	private long createNews(String guid) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			testNews.setGuid(guid);
			return newsDao.create(db, testNews);
		} finally {
			db.close();
		}
	}

	@SmallTest
	public void testReadNewsUseId() {
		long id = createNews("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			News n = newsDao.read(db, id);
			
			assertEquals(testNews, n);
			
			newsDao.delete(db, id);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testReadNewsUseGuid() {
		createNews("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			News n = newsDao.read(db, "guid1");

			assertEquals(testNews, n);
			
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testDeleteNewsUseGuid() {
		createNews("guid1");
		Iterator<Link> it = testNews.getLinks().iterator();
		long linkId1 = it.next().getId();
		long linkId2 = it.next().getId();
		long linkId3 = it.next().getId();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsLinksDao nlinksDao = new NewsLinksDao();
			NewsDao newsDao = new NewsDao();
			newsDao.delete(db, "guid1");
			News n = newsDao.read(db, "guid1");
			assertNull(n);
			Link l = nlinksDao.read(db, linkId1);
			assertNull(l);
			l = nlinksDao.read(db, linkId2);
			assertNull(l);
			l = nlinksDao.read(db, linkId3);
			assertNull(l);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testDeleteNewsUseId() {
		long id = createNews("guid1");
		Iterator<Link> it = testNews.getLinks().iterator();
		long linkId1 = it.next().getId();
		long linkId2 = it.next().getId();
		long linkId3 = it.next().getId();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsLinksDao nlinksDao = new NewsLinksDao();
			NewsDao newsDao = new NewsDao();
			newsDao.delete(db, id);
			News n = newsDao.read(db, id);
			assertNull(n);
			Link l = nlinksDao.read(db, linkId1);
			assertNull(l);
			l = nlinksDao.read(db, linkId2);
			assertNull(l);
			l = nlinksDao.read(db, linkId3);
			assertNull(l);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testUpdateNews() {
		long id = createNews("guid1");
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			NewsDao newsDao = new NewsDao();
			News n = newsDao.read(db, "guid1");
			assertEquals("News 1", n.getTitle());
	
			n.setBriefText("newbrief1");
			n.setFullText("newfull1");
			n.setGuid("newguid1");
			n.setImageCaption("newcaption1");
			n.setImageCredits("newcredits1");
			n.setImageLink("newimagelink1");
			n.setLink("newlink1");
			n.setPubDate(date);
			n.setRubric(Rubrics.ECONOMICS_FINANCE);
			n.setRubricUpdateNeed(false);
			n.setTitle("newtitle1");
			
			Iterator<Link> it = n.getLinks().iterator();
			Date dd = new Date();
			Link l = it.next();
			l.setDate(dd);
			l.setTitle("new title");
			l.setUrl("http://www.mail.ru");
			
			l = it.next();
			l.setDate(dd);
			l.setTitle("new title 1");
			l.setUrl("http://www.gmail.com");
			
			l = it.next();
			l.setDate(dd);
			l.setTitle("new title 2");
			l.setUrl("http://www.htc.com");
			
			newsDao.update(db, n);
			
			News n1 = newsDao.read(db, id);

			assertEquals(n, n1);
			
			newsDao.delete(db, "newguid1");
			n = newsDao.read(db, "newguid1");
			assertNull(n);
		} finally {
			db.close();
		}
	}
	
	@SmallTest
	public void testMoreNewsInDb() {
		List<String> ids = new ArrayList<String>();
		ids.add("guid1");
		ids.add("guid2");
		ids.add("guid3");
		ids.add("guid4");
		Collections.sort(ids);
		
		for (String id : ids) {
			createNews(id);
		}
		
		NewsDao newsDao = new NewsDao();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		try {
			Collection<String> allIds = newsDao.readAllKeys(db);
			List<String> sortedIds = new ArrayList<String>(allIds);
			Collections.sort(sortedIds);
			
			assertEquals(ids, sortedIds);
			
			for (String id : ids) {
				newsDao.delete(db, id);
			}
			
			allIds = newsDao.readAllKeys(db);
			assertEquals(allIds.size(), 0);
		} finally {
			db.close();
		}
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		context.deleteDatabase(LentaDbHelper.DATABASE_NAME);
	}
}
