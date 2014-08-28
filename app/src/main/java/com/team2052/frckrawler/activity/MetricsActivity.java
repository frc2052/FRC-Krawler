package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.dialog.EditMetricDialogActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.fragment.dialog.AddMetricFragment;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.MetricListElement;

import java.util.ArrayList;

public class MetricsActivity extends NewDatabaseActivity implements OnClickListener {

    public static final String METRIC_CATEGORY = "METRIC_CATEGORY";
    private int metricCategory; //Either MATCH_PERF_METRpublicOBOT_METRICS, or DRIVER_METRICS
    private int selectedMetricID;
    private DBManager dbManager;
    public Metric[] metrics;
    public DynamicListView mDynamicListView;
    public AlphaInAnimationAdapter mAdapter;
    private Game mGame;

    public static Intent newInstance(Context context, Game game, MetricType type) {
        Intent i = new Intent(context, MetricsActivity.class);
        i.putExtra(PARENT_ID, game.getId());
        i.putExtra(METRIC_CATEGORY, type.ordinal());
        return i;
    }

    public static enum MetricType {
        MATCH_PERF_METRICS("Match Performance Metrics"), ROBOT_METRICS("Pit Scout Metrics"), DRIVER_METRICS("Driver Metrics");
        public final String title;
        public static final MetricType[] VALID_TYPES = values();

        MetricType(String title) {
            this.title = title;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);
        metricCategory = getIntent().getIntExtra(METRIC_CATEGORY, -1);
        mGame = Game.load(Game.class, getIntent().getIntExtra(PARENT_ID, -1));
        getActionBar().setTitle(MetricType.VALID_TYPES[metricCategory].title);
        mDynamicListView = (DynamicListView) findViewById(R.id.metric_list);
        dbManager = DBManager.getInstance(this);
        new GetMetricsTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addbutton, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_metric_action) {
            AddMetricFragment fragment = AddMetricFragment.newInstance(metricCategory, mGame.name);
            fragment.show(getSupportFragmentManager(), "Add Metric");
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            startActivity(HomeActivity.newInstance(this, R.id.nav_item_games).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            //TODO REIMPLEMENT
                              /*case R.id.down:
                                  if (metrics.length == 0 || radioGroup.getSelectedButton() == null ||
                                          isGettingMetrics)
                                      break;
                                  else {
                                      int metricPos = 0;
                                      int metricID = ((Integer) radioGroup.getSelectedButton().getTag()).intValue();
                                      for (int k = 0; k < metrics.length; k++) {
                                          if (metrics[k].getID() == metricID)
                                              metricPos = k;
                                      }
                                      if (metricPos == metrics.length - 1)
                                          break;
                                      int lowerMetricPos = metricPos + 1;
                                      int lowerMetricID = metrics[lowerMetricPos].getID();
                                      if (metricCategory == MATCH_PERF_METRICS) {
                                          System.out.println(dbManager.flipMatchMetricPosition(metricID, lowerMetricID));
                                      } else if (metricCategory == ROBOT_METRICS) {
                                          dbManager.flipRobotMetricPosition(metricID, lowerMetricID);
                                      }
                                      new GetMetricsTask().execute();
                                      break;
                                  }

                              case R.id.up:
                                  if (metrics.length == 0 || radioGroup.getSelectedButton() == null ||
                                          isGettingMetrics)
                                      break;
                                  else {
                                      int metricPos = 0;
                                      int metricID = ((Integer) radioGroup.getSelectedButton().getTag()).intValue();
                                      for (int k = 0; k < metrics.length; k++) {
                                          if (metrics[k].getID() == metricID)
                                              metricPos = k;
                                      }
                                      if (metricPos == 0)
                                          break;
                                      int upperMetricPos = metricPos - 1;
                                      int upperMetricID = metrics[upperMetricPos].getID();
                                      if (metricCategory == MATCH_PERF_METRICS) {
                                          dbManager.flipMatchMetricPosition(metricID, upperMetricID);
                                      } else if (metricCategory == ROBOT_METRICS) {
                                          dbManager.flipRobotMetricPosition(metricID, upperMetricID);
                                      }
                                      new GetMetricsTask().execute();
                                      break;
                                  }

                              case SELECTED_BUTTON_ID:
                                  radioGroup.notifyClick((RadioButton) v);
                                  selectedMetricID = (Integer) v.getTag();
                                  break;*/
        }
    }

    public void editMetric(int metricId) {
        //TODO Fragment dialog?
        Intent i = new Intent(this, EditMetricDialogActivity.class);
        i.putExtra(EditMetricDialogActivity.METRIC_CATEGORY_EXTRA, metricCategory);
        i.putExtra(EditMetricDialogActivity.METRIC_ID_EXTRA, metricId);
        startActivity(i);
    }

    private class GetMetricsTask extends AsyncTask<Void, Void, ListViewAdapter> {
        private int metricNum;

        @Override
        protected void onPreExecute() {
            metricNum = 0;
        }

        @Override
        protected ListViewAdapter doInBackground(Void... v) {
            switch (MetricType.VALID_TYPES[metricCategory]) {
                case MATCH_PERF_METRICS:
                    //metrics = dbManager.getMatchPerformanceMetricsByColumns(new String[]{DBContract.COL_GAME_NAME}, new String[]{databaseValues[MetricsActivity.this.getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]});
                    break;

                case ROBOT_METRICS:
                    //metrics = dbManager.getRobotMetricsByColumns(new String[]{DBContract.COL_GAME_NAME}, new String[]{databaseValues[MetricsActivity.this.getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]});
                    break;

                default:
                    metrics = new Metric[0];
            }
            ArrayList<ListItem> listMetrics = new ArrayList<ListItem>();
            for (Metric metric : metrics) {
                listMetrics.add(new MetricListElement(metric, MetricsActivity.this));
            }
            ListViewAdapter adapter = new ListViewAdapter(MetricsActivity.this, listMetrics);
            return adapter;
        }

        @Override
        protected void onPostExecute(ListViewAdapter adapter) {
            mAdapter = new AlphaInAnimationAdapter(adapter);
            mAdapter.setAbsListView(mDynamicListView);
            mDynamicListView.setAdapter(mAdapter);
        }
    }
}
