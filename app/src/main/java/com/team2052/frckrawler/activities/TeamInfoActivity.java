package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.tab.ViewTeamPagerAdapter;
import com.team2052.frckrawler.db.Team;

/**
 * @author Adam
 */
public class TeamInfoActivity extends DatabaseActivity {
    ViewPager viewPager;
    TabLayout tabLayout;

    public static Intent newInstance(Context context, Team team) {
        Intent intent = new Intent(context, TeamInfoActivity.class);
        intent.putExtra(PARENT_ID, team.getNumber());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab);
        long team_id = getIntent().getLongExtra(PARENT_ID, 0);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager.setAdapter(new ViewTeamPagerAdapter(this, getSupportFragmentManager(), team_id));
        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setActionBarSubtitle(String.valueOf(team_id));
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
}
