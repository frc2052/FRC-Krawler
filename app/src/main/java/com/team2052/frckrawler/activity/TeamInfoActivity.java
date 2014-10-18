package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.team2052.frckrawler.fragment.robot.RobotsFragment;
import com.team2052.frckrawler.fragment.scout.NeedSyncFragment;
import com.team2052.frckrawler.fragment.team.ContactsFragment;

import frckrawler.Team;

/**
 * @author Adam
 */
public class TeamInfoActivity extends ViewPagerActivity
{
    private Team mTeam;

    public static Intent newInstance(Context context, Team team)
    {
        Intent intent = new Intent(context, TeamInfoActivity.class);
        intent.putExtra(PARENT_ID, team.getNumber());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mTeam = mDaoSession.getTeamDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle("Team");
        setActionBarSubtitle(mTeam.getName());
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
                    fragment = new NeedSyncFragment();
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
