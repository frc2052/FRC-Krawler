package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.fragment.ContactsFragment;
import com.team2052.frckrawler.fragment.RobotsFragment;
import com.team2052.frckrawler.fragment.ScoutHomeFragment;

/**
 * @author Adam
 */
public class TeamInfoActivity extends DatabaseActivity {
    private ViewPager mPager;
    private PagerSlidingTabStrip mTabs;
    private Team mTeam;

    public static Intent newInstance(Context context, Team team) {
        Intent intent = new Intent(context, TeamInfoActivity.class);
        intent.putExtra(PARENT_ID, team.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_info);
        mPager = (ViewPager) findViewById(R.id.pager);
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPager.setAdapter(new ViewTeamPagerAdapter(getSupportFragmentManager()));
        mTabs.setViewPager(mPager);
        mTeam = Team.load(Team.class, getIntent().getLongExtra(PARENT_ID, 0));
        getActionBar().setTitle("Team " + mTeam.number);
    }

    public class ViewTeamPagerAdapter extends FragmentPagerAdapter {
        public final String[] headers = {"Info", "Robots", "Contacts"};

        public ViewTeamPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return headers[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new ScoutHomeFragment();
                    break;
                case 1:
                    fragment = RobotsFragment.newInstance(mTeam);

                    break;
                case 2:
                    fragment = ContactsFragment.newInstance(mTeam);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return headers.length;
        }
    }
}
