package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.views.SlidingTabLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 */
public abstract class ViewPagerFragment extends BaseFragment {
    @InjectView(R.id.tabs)
    protected SlidingTabLayout mTabs;
    @InjectView(R.id.pager)
    protected ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.view_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        mTabs.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.color_accent));
        mViewPager.setAdapter(setAdapter());
        mTabs.setViewPager(mViewPager);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        mTabs.setVisibility(View.GONE);
        super.onDestroy();
    }

    public abstract PagerAdapter setAdapter();
}
