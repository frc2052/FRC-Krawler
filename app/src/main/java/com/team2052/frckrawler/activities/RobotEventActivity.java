package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.tab.RobotEventPagerAdapter;

public class RobotEventActivity extends DatabaseActivity {
    private static final String EVENT_ID = "EVENT_ID";
    ViewPager viewPager;
    TabLayout tabLayout;

    public static Intent newInstance(Context context, long robot_id, long event_id) {
        Intent intent = new Intent(context, RobotEventActivity.class);
        intent.putExtra(PARENT_ID, robot_id);
        intent.putExtra(EVENT_ID, event_id);
        return intent;
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        long robot_id = getIntent().getLongExtra(PARENT_ID, 0);
        long event_id = getIntent().getLongExtra(EVENT_ID, 0);

        setTitle(String.format("%d@%s", rxDbManager.getRobotsTable().load(robot_id).getTeam_id(), rxDbManager.getEventsTable().load(event_id).getName()));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager.setAdapter(new RobotEventPagerAdapter(this, getSupportFragmentManager(), robot_id, event_id));
        tabLayout.setupWithViewPager(viewPager);
    }
}
