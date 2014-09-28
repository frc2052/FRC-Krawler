package com.team2052.frckrawler.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.PagerAdapter;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.database.models.Event;

public class ScoutFragment extends ViewPagerFragment
{

    private Event mEvent;
    private boolean mNeedsSync;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        if (scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
            //Suggest the scout to sync again anyway.
            mEvent = Event.load(Event.class, scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
            mNeedsSync = false;
        } else {
            mNeedsSync = true;
        }

    }

    @Override
    public PagerAdapter setAdapter()
    {
        return new ScoutPagerAdapter(getFragmentManager());
    }


    public class ScoutPagerAdapter extends FragmentPagerAdapter
    {
        public final String[] headers = {"Home", "Match Scouting", "Pit Scouting", "Schedule"};

        public ScoutPagerAdapter(FragmentManager fm)
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
                    if (!mNeedsSync) {
                        fragment = ScoutMatchFragment.newInstance(mEvent);
                    } else {
                        fragment = new NeedSyncFragment();
                    }
                    break;
                case 2:
                    if (!mNeedsSync) {
                        fragment = ScoutPitFragment.newInstance(mEvent);
                    } else {
                        fragment = new NeedSyncFragment();
                    }
                    break;
                case 3:
                    if (!mNeedsSync) {
                        fragment = MatchListFragment.newInstance(mEvent);
                    } else {
                        fragment = new NeedSyncFragment();
                    }
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
