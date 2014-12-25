package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.fragment.PhotosFragment;
import com.team2052.frckrawler.fragment.RobotAttendingEventsFragment;

/**
 * @author Adam
 */
public class RobotActivity extends ViewPagerActivity
{
    private Robot mRobot;

    public static Intent newInstance(Context context, long rKey)
    {
        Intent intent = new Intent(context, RobotActivity.class);
        intent.putExtra(PARENT_ID, rKey);
        return intent;
    }

    @Override
    public void onPreLoadViewPager()
    {
        mRobot = mDaoSession.getRobotDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle(getString(R.string.robot_text));
        setActionBarSubtitle(String.valueOf(mRobot.getTeamId()));
    }

    @Override
    public PagerAdapter setAdapter()
    {
        return new RobotViewPagerAdapter(getSupportFragmentManager());
    }

    public class RobotViewPagerAdapter extends FragmentPagerAdapter
    {
        private String[] HEADERS = getResources().getStringArray(R.array.robot_tab_titles);

        public RobotViewPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return HEADERS[position];
        }

        @Override
        public Fragment getItem(int position)
        {
            switch (position) {
                /*case 0:
                    return new NeedSyncFragment();*/
                case 0:
                    return RobotAttendingEventsFragment.newInstance(mRobot);
                case 1:
                    return PhotosFragment.newInstance(mRobot);
            }
            return null;
        }

        @Override
        public int getCount()
        {
            return HEADERS.length;
        }
    }
}
