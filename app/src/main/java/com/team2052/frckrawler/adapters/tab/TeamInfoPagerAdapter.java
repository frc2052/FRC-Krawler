package com.team2052.frckrawler.adapters.tab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.fragments.team.TeamInfoFragment;

/**
 * Created by Adam on 11/23/2015.
 */
public class TeamInfoPagerAdapter extends FragmentPagerAdapter {
    public final String[] headers;
    private long team_id;

    public TeamInfoPagerAdapter(Context context, FragmentManager fm, long team_id) {
        super(fm);
        this.headers = context.getResources().getStringArray(R.array.team_tab_titles);
        this.team_id = team_id;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = TeamInfoFragment.Companion.newInstance(team_id);
                break;
        }
        return fragment;
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
