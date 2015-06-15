package com.team2052.frckrawler.activities;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.views.SlidingTabLayout;

/**
 * @author Adam
 */
@Deprecated
public abstract class ViewPagerActivity extends BaseActivity {
    protected ViewPager mPager;
    protected SlidingTabLayout mTabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setVisibility(View.VISIBLE);
        mTabs.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.color_accent));
        onPreLoadViewPager();
        mPager.setAdapter(setAdapter());
        mTabs.setViewPager(mPager);
    }

    public void onPreLoadViewPager() {
    }

    /**
     * @return The adapter that you want to attach to the ViewPager and the Tabs
     */
    public abstract PagerAdapter setAdapter();
}
