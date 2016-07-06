package com.team2052.frckrawler.adapters.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.team2052.frckrawler.fragments.MetricInfoFragment;

public class MetricInfoPagerAdapter extends FragmentPagerAdapter {
    final long metricId;
    public String[] headers = new String[]{"Info"};

    public MetricInfoPagerAdapter(FragmentManager fragmentManager, long metricId) {
        super(fragmentManager);
        this.metricId = metricId;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MetricInfoFragment.newInstance(metricId);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return headers[position];
    }

    @Override
    public int getCount() {
        return headers.length;
    }
}
