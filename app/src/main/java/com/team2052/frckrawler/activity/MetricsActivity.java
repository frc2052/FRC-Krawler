package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.fragment.dialog.AddMetricFragment;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.MetricListElement;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import frckrawler.Event;
import frckrawler.Game;
import frckrawler.Metric;
import frckrawler.MetricDao;

public class MetricsActivity extends ListActivity
{

    public static final String METRIC_CATEGORY = "METRIC_CATEGORY";
    private int metricCategory;

    private int mCurrentSelectedItem;
    private final ActionMode.Callback callback = new ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu)
        {
            long metricId = Long.parseLong(((ListElement) mAdapter.getItem(mCurrentSelectedItem)).getKey());
            Metric metric = mDaoSession.getMetricDao().load(metricId);
            actionMode.getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
            menu.removeItem(R.id.menu_edit);
            actionMode.setTitle(metric.getName());
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem)
        {
            long metricId = Long.parseLong(((ListElement) mAdapter.getItem(mCurrentSelectedItem)).getKey());
            Metric metric = mDaoSession.getMetricDao().load(metricId);
            switch (menuItem.getItemId()) {
                case R.id.menu_delete:
                    DBManager.deleteMetric(mDaoSession, metric);
                    updateList();
                    actionMode.finish();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode)
        {
            mCurrentActionMode = null;
        }
    };

    private Game mGame;
    private ActionMode mCurrentActionMode;

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
        super.onCreate(savedInstanceState);
        mGame = mDaoSession.getGameDao().load(getIntent().getLongExtra(PARENT_ID, -1));
        metricCategory = getIntent().getIntExtra(METRIC_CATEGORY, -1);
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                if(mCurrentActionMode != null){return false;}
                mCurrentSelectedItem = i;
                mCurrentActionMode = startActionMode(callback);
                return true;
            }
        });
        setActionBarTitle(MetricType.VALID_TYPES[metricCategory].title);
        setActionBarSubtitle(mGame.getName());
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
            WhereCondition eq = MetricDao.Properties.Category.eq(metricCategory);
            QueryBuilder<Metric> metricQueryBuilder = mDaoSession.getMetricDao().queryBuilder();
            metricQueryBuilder.and(MetricDao.Properties.Category.eq(metricCategory), MetricDao.Properties.GameId.eq(mGame.getId()));
            List<Metric> metrics = metricQueryBuilder.list();
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
