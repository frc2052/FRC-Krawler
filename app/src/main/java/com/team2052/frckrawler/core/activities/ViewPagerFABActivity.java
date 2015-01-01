package com.team2052.frckrawler.core.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.ui.SlidingTabLayout;

/**
 * @author Adam
 * @since 10/18/2014
 */
public abstract class ViewPagerFABActivity extends ViewPagerActivity {
    protected ImageButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_fab);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.color_accent));
        mTabs.setVisibility(View.VISIBLE);
        mFab = (ImageButton) findViewById(R.id.fab);
        onPreLoadViewPager();
        mPager.setAdapter(setAdapter());
        mTabs.setViewPager(mPager);
    }

    protected void setFabIcon(int resId) {
        mFab.setImageResource(resId);
    }
}
