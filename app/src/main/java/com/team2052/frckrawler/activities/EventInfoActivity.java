package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.tab.EventViewPagerAdapter;
import com.team2052.frckrawler.listeners.FABButtonListener;

/**
 * @author Adam
 * @since 10/16/2014
 */
public class EventInfoActivity extends DatabaseActivity implements View.OnClickListener {
    ViewPager mViewPager;
    TabLayout mTabLayout;

    private EventViewPagerAdapter mAdapter;
    private FloatingActionButton mFab;

    public static Intent newInstance(Context context, long event_id) {
        Intent intent = new Intent(context, EventInfoActivity.class);
        intent.putExtra(PARENT_ID, event_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long mEvent_id = getIntent().getLongExtra(PARENT_ID, 0);
        setContentView(R.layout.layout_tab_fab);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mFab.setOnClickListener(this);

        mViewPager.setAdapter(mAdapter = new EventViewPagerAdapter(getSupportFragmentManager(), mEvent_id));
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position < 3) {
                    mFab.hide();
                } else {
                    mFab.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floating_action_button) {
            if (mAdapter.getRegisteredFragment(mViewPager.getCurrentItem()) instanceof FABButtonListener) {
                ((FABButtonListener) mAdapter.getRegisteredFragment(mViewPager.getCurrentItem())).onFABPressed();
            }
        }
    }
}
