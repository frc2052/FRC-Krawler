package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.InstanceFragmentStatePagerAdapter;
import com.team2052.frckrawler.fragments.event.EventInfoFragment;
import com.team2052.frckrawler.fragments.match.MatchListFragment;
import com.team2052.frckrawler.fragments.metric.SummaryFragment;
import com.team2052.frckrawler.fragments.team.RobotsFragment;
import com.team2052.frckrawler.listeners.RefreshListener;

/**
 * @author Adam
 * @since 10/16/2014
 */
public class EventInfoActivity extends DatabaseActivity {
    ViewPager mViewPager;
    TabLayout mTabLayout;

    private long mEvent_id;
    private EventViewPagerAdapter mAdapter;

    public static Intent newInstance(Context context, long event_id) {
        Intent intent = new Intent(context, EventInfoActivity.class);
        intent.putExtra(PARENT_ID, event_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent_id = getIntent().getLongExtra(PARENT_ID, 0);
        setContentView(R.layout.layout_tab);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setActionBarTitle(getString(R.string.event));

        mViewPager.setAdapter(mAdapter = new EventViewPagerAdapter());
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        });
    }

    @Override
    public void inject() {
        getComponent().inject(this);
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
                    return EventInfoFragment.newInstance(mEvent_id);
                case 1:
                    return SummaryFragment.newInstance(mEvent_id);
                case 2:
                    return MatchListFragment.newInstance(mEvent_id);
                case 3:
                    return RobotsFragment.newInstance(mEvent_id);
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
