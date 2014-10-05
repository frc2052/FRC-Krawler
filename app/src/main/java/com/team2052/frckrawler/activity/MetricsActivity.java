package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.metric.Metric;
import com.team2052.frckrawler.fragment.dialog.AddMetricFragment;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.MetricListElement;

import java.util.ArrayList;
import java.util.List;

public class MetricsActivity extends ListActivity
{

    public static final String METRIC_CATEGORY = "METRIC_CATEGORY";
    private int metricCategory;

    private Game mGame;

    public static Intent newInstance(Context context, Game game, MetricType type)
    {
        Intent i = new Intent(context, MetricsActivity.class);
        i.putExtra(PARENT_ID, game.getId());
        i.putExtra(METRIC_CATEGORY, type.ordinal());
        return i;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mGame = Game.load(Game.class, getIntent().getLongExtra(PARENT_ID, -1));
        metricCategory = getIntent().getIntExtra(METRIC_CATEGORY, -1);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        super.onCreate(savedInstanceState);
        setActionBarTitle(MetricType.VALID_TYPES[metricCategory].title);
        setActionBarSubtitle(mGame.name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.addbutton, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.add_action) {
            AddMetricFragment fragment = AddMetricFragment.newInstance(metricCategory, mGame);
            fragment.show(getSupportFragmentManager(), "Add Metric");
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            startActivity(HomeActivity.newInstance(this, R.id.nav_item_games).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateList()
    {
        new GetMetricsTask().execute();
    }

    public static enum MetricType
    {
        MATCH_PERF_METRICS("Match Performance Metrics"), ROBOT_METRICS("Pit Scout Metrics"), DRIVER_METRICS("Driver Metrics");
        public static final MetricType[] VALID_TYPES = values();
        public final String title;

        MetricType(String title)
        {
            this.title = title;
        }
    }

    private class GetMetricsTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... v)
        {
            List<Metric> metrics = DBManager.loadFromMetrics().where("Game = ?", mGame.getId()).and("Category = ?", metricCategory).execute();
            ArrayList<ListItem> listMetrics = new ArrayList<>();
            for (Metric metric : metrics) {
                listMetrics.add(new MetricListElement(metric));
            }
            mAdapter = new ListViewAdapter(MetricsActivity.this, listMetrics);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            mListView.setAdapter(mAdapter);
        }
    }
}
