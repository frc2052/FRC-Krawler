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
import com.team2052.frckrawler.adapters.tab.GameInfoPagerAdapter;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listeners.RefreshListener;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class GameInfoActivity extends DatabaseActivity implements View.OnClickListener {

    ViewPager mViewPager;
    TabLayout mTabLayout;
    FloatingActionButton mFab;

    private GameInfoPagerAdapter mAdapter;

    public static Intent newInstance(Context context, long game_id) {
        Intent intent = new Intent(context, GameInfoActivity.class);
        intent.putExtra(PARENT_ID, game_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab_fab);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mFab = (FloatingActionButton) findViewById(R.id.floating_action_button);
        mFab.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new GameInfoPagerAdapter(getSupportFragmentManager(), getIntent().getLongExtra(PARENT_ID, 0));
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mFab.hide();
                    ((RefreshListener) mAdapter.getRegisteredFragment(0)).refresh();
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
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floating_action_button) {
            ((FABButtonListener) mAdapter.getRegisteredFragment(mViewPager.getCurrentItem())).onFABPressed();
        }
    }

}
