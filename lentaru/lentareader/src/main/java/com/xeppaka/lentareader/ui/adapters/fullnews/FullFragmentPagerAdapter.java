package com.xeppaka.lentareader.ui.adapters.fullnews;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.util.ArrayMap;
import android.view.ViewGroup;

import com.xeppaka.lentareader.ui.fragments.FullFragmentBase;

import java.util.List;

/**
 * Created by nnm on 3/29/14.
 */
public abstract class FullFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private List<Long> ids;
    private ArrayMap<Integer, FullFragmentBase> positionMap;

    public FullFragmentPagerAdapter(FragmentManager fm, List<Long> ids) {
        super(fm);

        this.ids = ids;

        positionMap = new ArrayMap<Integer, FullFragmentBase>(3);
    }

    @Override
    public FullFragmentBase getItem(int position) {
        FullFragmentBase fragment = positionMap.get(position);

        if (fragment == null) {
            fragment = createFragment();
            fragment.setDbId(ids.get(position));
        }

        return fragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object result = super.instantiateItem(container, position);

        if (result instanceof FullFragmentBase) {
            positionMap.put(position, (FullFragmentBase) result);
        }

        return result;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);

        positionMap.remove(position);
    }

    @Override
    public int getCount() {
        return ids.size();
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public abstract FullFragmentBase createFragment();
}
