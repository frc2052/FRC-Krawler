package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.InstanceFragmentStatePagerAdapter;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragments.event.EventInfoFragment;
import com.team2052.frckrawler.fragments.match.MatchListFragment;
import com.team2052.frckrawler.fragments.metric.SummaryFragment;
import com.team2052.frckrawler.fragments.team.RobotsFragment;

/**
 * @author Adam
 * @since 10/16/2014
 */
public class EventInfoActivity extends BaseActivity {
    private Event mEvent;
    private EventViewPagerAdapter mAdapter;

    public static Intent newInstance(Context context, Event event) {
        Intent intent = new Intent(context, EventInfoActivity.class);
        intent.putExtra(PARENT_ID, event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = mDbManager.getEventsTable().load(getIntent().getLongExtra(PARENT_ID, 0));

        if (mEvent == null) {
            finish();
        }

        /*binding = DataBindingUtil.setContentView(this, R.layout.layout_tab);
        binding.viewPager.setAdapter(mAdapter = new EventViewPagerAdapter());
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    ((RefreshListener) mAdapter.getRegisteredFragment(0)).refresh();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/

        //setSupportActionBar(binding.toolbar);
        setActionBarTitle(getString(R.string.event));
        setActionBarSubtitle(mEvent.getName());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    public class EventViewPagerAdapter extends InstanceFragmentStatePagerAdapter {
        public String[] headers = new String[]{"Info", "Metric Summary", "Schedule", "Attending"};

        public EventViewPagerAdapter() {
            super(getSupportFragmentManager());
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EventInfoFragment.newInstance(mEvent);
                case 1:
                    return SummaryFragment.newInstance(mEvent);
                case 2:
                    return MatchListFragment.newInstance(mEvent);
                case 3:
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
