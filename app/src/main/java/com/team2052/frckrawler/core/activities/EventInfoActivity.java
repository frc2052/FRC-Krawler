package com.team2052.frckrawler.core.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.fragments.MatchListFragment;
import com.team2052.frckrawler.core.fragments.RobotsFragment;
import com.team2052.frckrawler.core.fragments.SummaryFragment;
import com.team2052.frckrawler.db.Event;

/**
 * @author Adam
 * @since 10/16/2014
 */
public class EventInfoActivity extends ViewPagerActivity {
    private Event mEvent;

    public static Intent newInstance(Context context, Event event) {
        Intent intent = new Intent(context, EventInfoActivity.class);
        intent.putExtra(PARENT_ID, event.getId());
        return intent;
    }

    @Override
    public void onPreLoadViewPager() {
        mEvent = mDaoSession.getEventDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle(getString(R.string.event));
        setActionBarSubtitle(mEvent.getName());
    }

    @Override
    public PagerAdapter setAdapter() {
        return new EventViewPagerAdapter(getSupportFragmentManager());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return false;
    }

    public class EventViewPagerAdapter extends FragmentPagerAdapter {
        public String[] headers = new String[]{"Metric Summary", "Schedule", "Attending"};

        public EventViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return SummaryFragment.newInstance(mEvent);
                case 1:
                    return MatchListFragment.newInstance(mEvent);
                case 2:
                    return RobotsFragment.newInstance(mEvent);
            }
            return null;
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