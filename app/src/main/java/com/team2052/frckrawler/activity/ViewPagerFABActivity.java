package com.team2052.frckrawler.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.view.SlidingTabLayout;

/**
 * @author Adam
 * @since 10/18/2014
 */
public abstract class ViewPagerFABActivity extends ViewPagerActivity
{
    protected ImageButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager_fab);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);

        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer()
        {
            @Override
            public int getIndicatorColor(int position)            {
                return position % 2 == 0 ? getResources().getColor(R.color.red900) : Color.YELLOW;
            }
        });

        mFab = (ImageButton) findViewById(R.id.fab);
        onPreLoadViewPager();
        mPager.setAdapter(setAdapter());
        mTabs.setViewPager(mPager);
    }

    protected void setFabIcon(int resId)
    {
        mFab.setImageResource(resId);
    }
}
