package com.team2052.frckrawler.core.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.fragments.RobotsFragment;
import com.team2052.frckrawler.db.Team;

/**
 * @author Adam
 */
public class TeamInfoActivity extends ViewPagerActivity {
    private Team mTeam;

    public static Intent newInstance(Context context, Team team) {
        Intent intent = new Intent(context, TeamInfoActivity.class);
        intent.putExtra(PARENT_ID, team.getNumber());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTeam = mDaoSession.getTeamDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle(getString(R.string.team));
        setActionBarSubtitle(String.valueOf(mTeam.getNumber()));
    }

    @Override
    public PagerAdapter setAdapter() {
        return new ViewTeamPagerAdapter(getSupportFragmentManager());
    }

    public class ViewTeamPagerAdapter extends FragmentPagerAdapter {
        public final String[] headers = getResources().getStringArray(R.array.team_tab_titles);

        public ViewTeamPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {/*
                case 0:
                    fragment = new NeedSyncFragment();
                    break;*/
                case 0:
                    fragment = RobotsFragment.newInstance(mTeam);
                    break;
/*                case 2:
                    fragment = ContactsFragment.newInstance(mTeam);
                    break;*/
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
}
