package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.tab.MetricInfoPagerAdapter;
import com.team2052.frckrawler.db.Metric;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 6/13/2015.
 */
public class MetricInfoActivity extends DatabaseActivity {
    public static final String METRIC_ID = "METRIC_ID";
    private Metric mMetirc;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private MetricInfoPagerAdapter mAdapter;

    public static Intent newInstance(Context context, long metric_id) {
        Intent intent = new Intent(context, MetricInfoActivity.class);
        intent.putExtra(METRIC_ID, metric_id);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab_fab);
        mTabLayout = (TabLayout) findViewById(R.id.tab_layout);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mMetirc = rxDbManager.getMetricsTable().load(getIntent().getLongExtra(METRIC_ID, 0));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAdapter = new MetricInfoPagerAdapter(getSupportFragmentManager(), mMetirc.getId());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        
        if (item.getItemId() == R.id.menu_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Delete Metric?");
            builder.setMessage("Are you sure you want to delete this metric? You will lose all data associated with this metric.");
            builder.setPositiveButton("Delete", (dialog, which) -> {
                Observable.just(mMetirc)
                        .map(metric -> {
                            rxDbManager.getMetricsTable().delete(metric);
                            return metric;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onNext -> {
                            finish();
                        });
            });
            builder.setNegativeButton("Cancel", null);
            builder.create().show();
        }
        
        /*
            Added a user prompt to edit the metrics when that is corrected. I also added a messages stating that the edit
            feature is currently under development to aviod the question of why is it not working.
        */
         if (item.getItemId() == R.id.menu_edit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edit Metric");
            builder.setMessage("Are you sure you want to edit this metric?");
            builder.setNegativeButton("Edit", (dialog, which) -> {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("Please have patience...");
                builder2.setMessage("This feature is currently under active development. It will be working in a future release. Thank you for your patience.");
                builder2.create().show();
                /*
                Replace lines 102-105 with the content of line 109
                */
                //EditMetricDialogFragment.newInstance(mMetirc).show(getSupportFragmentManager(), "editMetric");
            });
            builder.setPositiveButton("Cancel", null);
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }
}
