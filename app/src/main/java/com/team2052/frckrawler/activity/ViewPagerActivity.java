package com.team2052.frckrawler.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.team2052.frckrawler.R;

/**
 * @author Adam
 */
public abstract class ViewPagerActivity extends DatabaseActivity
{
    private ViewPager mPager;
    private PagerSlidingTabStrip mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pager);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager.setAdapter(setAdapter());
        mTabs.setViewPager(mPager);
    }

    /**
     * @return The adapter that you want to attach to the ViewPager and the Tabs
     */
    public abstract PagerAdapter setAdapter();
}
