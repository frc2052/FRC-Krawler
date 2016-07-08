package com.team2052.frckrawler.adapters.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.team2052.frckrawler.adapters.InstanceFragmentStatePagerAdapter;
import com.team2052.frckrawler.database.metric.MetricHelper;
import com.team2052.frckrawler.fragments.EventsFragment;
import com.team2052.frckrawler.fragments.GameInfoFragment;
import com.team2052.frckrawler.fragments.MetricsFragment;

/**
 * Created by Acorp on 11/17/2015.
 */
public class GameInfoPagerAdapter extends InstanceFragmentStatePagerAdapter {
    private final long mGameId;
    public String[] headers = new String[]{"Info", "Events", "Match Metrics", "Pit Metrics"};

    public GameInfoPagerAdapter(FragmentManager fm, long game_id) {
        super(fm);
        mGameId = game_id;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return GameInfoFragment.newInstance(mGameId);
            case 1:
                return EventsFragment.newInstance(mGameId);
            case 2:
                return MetricsFragment.newInstance(MetricHelper.MATCH_PERF_METRICS, mGameId);
            case 3:
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
