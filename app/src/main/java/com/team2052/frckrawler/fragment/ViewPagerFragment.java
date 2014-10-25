package com.team2052.frckrawler.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.view.SlidingTabLayout;

/**
 * @author Adam
 */
public abstract class ViewPagerFragment extends BaseFragment
{
    protected SlidingTabLayout mTabs;
    protected ViewPager mViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.pager, null);
        mTabs = (SlidingTabLayout) getActivity().findViewById(R.id.tabs);
        mTabs.setVisibility(View.VISIBLE);
        mTabs.setCustomTabView(R.layout.tab_indicator, android.R.id.text1);
        mTabs.setSelectedIndicatorColors(Color.WHITE);
        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        mViewPager.setAdapter(setAdapter());
        mTabs.setViewPager(mViewPager);
        return view;
    }

    public abstract PagerAdapter setAdapter();

    @Override
    public void onDestroy()
    {
        mTabs.setVisibility(View.GONE);
        super.onDestroy();
    }
}
