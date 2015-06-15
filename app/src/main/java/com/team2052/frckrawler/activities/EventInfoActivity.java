package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.InstanceFragmentStatePagerAdapter;
import com.team2052.frckrawler.databinding.LayoutTabBinding;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragments.match.MatchListFragment;
import com.team2052.frckrawler.fragments.metric.SummaryFragment;
import com.team2052.frckrawler.fragments.team.RobotsFragment;

/**
 * @author Adam
 * @since 10/16/2014
 */
public class EventInfoActivity extends BaseActivity {
    private Event mEvent;
    private LayoutTabBinding binding;

    public static Intent newInstance(Context context, Event event) {
        Intent intent = new Intent(context, EventInfoActivity.class);
        intent.putExtra(PARENT_ID, event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = mDbManager.mEvents.load(getIntent().getLongExtra(PARENT_ID, 0));

        if (mEvent == null) {
            finish();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.layout_tab);
        binding.viewPager.setAdapter(new EventViewPagerAdapter());
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        setSupportActionBar(binding.toolbar);
        setActionBarTitle(getString(R.string.event));
        setActionBarSubtitle(mEvent.getName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

    public class EventViewPagerAdapter extends InstanceFragmentStatePagerAdapter {
        public String[] headers = new String[]{"Metric Summary", "Schedule", "Attending"};

        public EventViewPagerAdapter() {
            super(getSupportFragmentManager());
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
