package com.team2052.frckrawler.adapters.tab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.fragments.RobotAttendingEventsFragment;

/**
 * Created by Adam on 11/23/2015.
 */
public class RobotViewPagerAdapter extends FragmentPagerAdapter {
    private String[] HEADERS;
    private long robot_id;

    public RobotViewPagerAdapter(Context context, FragmentManager fm, long robot_id) {
        super(fm);
        this.robot_id = robot_id;
        HEADERS = context.getResources().getStringArray(R.array.robot_tab_titles);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            /*case 0:
                return new NeedSyncFragment();*/
            case 0:
                return RobotAttendingEventsFragment.newInstance(robot_id);
            /*case 1:
                return PhotosFragment.newEventInstance(mRobot);*/
        }
        return null;
    }

    @Override
    public int getCount() {
        return HEADERS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return HEADERS[position];
    }
}
