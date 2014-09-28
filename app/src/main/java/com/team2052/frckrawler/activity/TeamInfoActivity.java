package com.team2052.frckrawler.activity;

import android.content.*;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.PagerAdapter;

import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.fragment.*;

/**
 * @author Adam
 */
public class TeamInfoActivity extends ViewPagerActivity
{
    private Team mTeam;

    public static Intent newInstance(Context context, Team team)
    {
        Intent intent = new Intent(context, TeamInfoActivity.class);
        intent.putExtra(PARENT_ID, team.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTeam = Team.load(Team.class, getIntent().getLongExtra(PARENT_ID, 0));
        getActionBar().setTitle("Team " + mTeam.number);
    }

    @Override
    public PagerAdapter setAdapter()
    {
        return new ViewTeamPagerAdapter(getSupportFragmentManager());
    }

    public class ViewTeamPagerAdapter extends FragmentPagerAdapter
    {
        public final String[] headers = {"Info", "Robots", "Contacts"};

        public ViewTeamPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return headers[position];
        }

        @Override
        public Fragment getItem(int position)
        {
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
        public int getCount()
        {
            return headers.length;
        }
    }
}
