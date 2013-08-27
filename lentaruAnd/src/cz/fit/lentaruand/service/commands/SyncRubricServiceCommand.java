package cz.fit.lentaruand.service.commands;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.ResultReceiver;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.async.AsyncNODao;
import cz.fit.lentaruand.data.dao.objects.NewsDao;

public class SyncRubricServiceCommand extends RunnableServiceCommand {
	private Rubrics rubric;
	private NewsType newsType;
	private ExecutorService executor;
	private ContentResolver contentResolver;
	
	public SyncRubricServiceCommand(int requestId, Rubrics rubric, NewsType newsType, ExecutorService executor, ContentResolver contentResolver, 
			ResultReceiver resultReceiver, boolean reportError) {
		super(requestId, resultReceiver, reportError);
		
		if (rubric == null) {
			throw new NullPointerException("rubric is null.");
		}
		
		if (newsType == null) {
			throw new NullPointerException("newsType is null.");
		}
		
		if (executor == null) {
			throw new NullPointerException("executor is null.");
		}
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}
		
		this.rubric = rubric;
		this.newsType = newsType;
		this.executor = executor;
		this.contentResolver = contentResolver;
	}

	@Override
	public void execute() throws Exception {
		switch (newsType) {
		case NEWS:
			syncNews(rubric);
			break;
		default:
			throw new UnsupportedOperationException("sync is unsupported for news of type: " + newsType.name() + " by now.");
		}
	}

	private void syncNews(Rubrics rubric) {
		AsyncNODao<News> newsDao = NewsDao.getInstance(contentResolver);
		Collection<News> news = newsDao.readForRubric(rubric);
		
		for (News newsObject : news) {
			executor.execute(new SyncNewsServiceCommand(getRequestId(), newsObject, contentResolver, executor, getResultReceiver(), false));
		}
	}
	
	@Override
	protected Bundle getResult() {
		return null;
	}
}
