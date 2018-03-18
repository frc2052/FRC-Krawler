package com.team2052.frckrawler.adapters.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.team2052.frckrawler.adapters.ViewOnClickFragmentStatePagerAdapter;
import com.team2052.frckrawler.fragments.metric.MetricsFragment;
import com.team2052.frckrawler.helpers.metric.MetricHelper;

/**
 * Created by Acorp on 11/17/2015.
 */
public class GameInfoPagerAdapter extends ViewOnClickFragmentStatePagerAdapter {
    private final long mGameId;
    public String[] headers = new String[]{"Match Metrics", "Pit Metrics"};

    public GameInfoPagerAdapter(FragmentManager fm, long game_id) {
        super(fm);
        mGameId = game_id;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return MetricsFragment.newInstance(MetricHelper.MATCH_PERF_METRICS, mGameId);
            case 1:
                return MetricsFragment.newInstance(MetricHelper.ROBOT_METRICS, mGameId);
        }
        return null;
    }

    @Override
    public int getCount() {
        return headers.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return headers[position];
    }
}
