package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.tab.RobotViewPagerAdapter;
import com.team2052.frckrawler.db.Robot;

/**
 * @author Adam
 */
public class RobotActivity extends DatabaseActivity {
    private Robot mRobot;
    ViewPager viewPager;
    TabLayout tabLayout;

    public static Intent newInstance(Context context, long rKey) {
        Intent intent = new Intent(context, RobotActivity.class);
        intent.putExtra(PARENT_ID, rKey);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mRobot = mDbManager.getRobotsTable().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle(getString(R.string.robot_text));
        setActionBarSubtitle(String.valueOf(mRobot.getTeam_id()));

        viewPager.setAdapter(new RobotViewPagerAdapter(this, getSupportFragmentManager(), mRobot.getId()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

}
