package com.team2052.frckrawler.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.team2052.frckrawler.R;

/**
 * @author Adam
 */
public abstract class ViewPagerFragment extends Fragment
{
    protected PagerSlidingTabStrip mTabs;
    protected ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.pager, null);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(setAdapter());
        mTabs.setViewPager(mViewPager);
        return view;
    }

    public abstract PagerAdapter setAdapter();
}
