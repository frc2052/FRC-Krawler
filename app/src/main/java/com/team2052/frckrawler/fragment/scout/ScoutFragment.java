package com.team2052.frckrawler.fragment.scout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.team2052.frckrawler.R;

public class ScoutFragment extends Fragment {

    private ViewPager mPager;
    private PagerSlidingTabStrip mTabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scout, null);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mPager.setAdapter(new ScoutPagerAdapter(getFragmentManager()));
        mTabs.setViewPager(mPager);
        /*ScoutJoinDialogFragment fragment = new ScoutJoinDialogFragment();
        fragment.show(getFragmentManager(), "JoinServer");*/
        return view;
    }

    public class ScoutPagerAdapter extends FragmentPagerAdapter {
        public final String[] headers = {"Home", "Match Scouting", "Pit Scouting", "Summary"};

        public ScoutPagerAdapter(FragmentManager fm) {
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
                    fragment = new ScoutTypeFragment();
                    break;
                case 2:
                    fragment = new ScoutTypeFragment();
                    break;
                case 3:
                    fragment = new ScoutHomeFragment();
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
