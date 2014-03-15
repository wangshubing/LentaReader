package com.xeppaka.lentareader.ui.fragments;

import android.app.Activity;

import com.xeppaka.lentareader.data.News;
import com.xeppaka.lentareader.data.dao.async.AsyncDao;
import com.xeppaka.lentareader.data.dao.daoobjects.NewsDao;

public class NewsFullFragment extends NewsObjectFullFragment<News> {
    private AsyncDao<News> dao;

    public NewsFullFragment() {}

    public NewsFullFragment(long id) {
        super(id);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        dao = NewsDao.getInstance(activity.getContentResolver());
    }

    @Override
    public AsyncDao<News> getDao() {
        return dao;
    }
}
