package com.team2052.frckrawler.adapters.tab;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.fragments.RobotEventMatchesFragment;
import com.team2052.frckrawler.fragments.robot.RobotEventSummaryFragment;

public class RobotEventPagerAdapter extends FragmentPagerAdapter {
    private String[] HEADERS;
    private long robot_id;
    private long event_id;


    public RobotEventPagerAdapter(Context context, FragmentManager fm, long robot_id, long event_id) {
        super(fm);
        this.robot_id = robot_id;
        this.event_id = event_id;
        HEADERS = context.getResources().getStringArray(R.array.robot_event_tab_titles);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RobotEventSummaryFragment.newInstance(robot_id, event_id);
            /*case 1:
                return RobotEventMatchesFragment.newInstance(robot_id, event_id);*/
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
