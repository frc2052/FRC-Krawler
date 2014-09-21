package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.fragment.dialog.AddMetricFragment;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.MetricListElement;

import java.util.ArrayList;
import java.util.List;

public class MetricsActivity extends DatabaseActivity implements OnClickListener {

    public static final String METRIC_CATEGORY = "METRIC_CATEGORY";
    private int metricCategory;

    private Game mGame;
    private ListView mListView;

    public static Intent newInstance(Context context, Game game, MetricType type) {
        Intent i = new Intent(context, MetricsActivity.class);
        i.putExtra(PARENT_ID, game.getId());
        i.putExtra(METRIC_CATEGORY, type.ordinal());
        return i;
    }

    public void updateMetricList() {
        new GetMetricsTask().execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);
        mGame = Game.load(Game.class, getIntent().getLongExtra(PARENT_ID, -1));
        getActionBar().setTitle(MetricType.VALID_TYPES[metricCategory].title);
        metricCategory = getIntent().getIntExtra(METRIC_CATEGORY, -1);
        mListView = (ListView) findViewById(R.id.metric_list);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addbutton, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetMetricsTask().execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        new GetMetricsTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                                      if (category == MATCH_PERF_METRICS) {
                                          System.out.println(dbManager.flipMatchMetricPosition(metricID, lowerMetricID));
                                      } else if (category == ROBOT_METRICS) {
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
                                      if (category == MATCH_PERF_METRICS) {
                                          dbManager.flipMatchMetricPosition(metricID, upperMetricID);
                                      } else if (category == ROBOT_METRICS) {
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

    public static enum MetricType {
        MATCH_PERF_METRICS("Match Performance Metrics"), ROBOT_METRICS("Pit Scout Metrics"), DRIVER_METRICS("Driver Metrics");
        public static final MetricType[] VALID_TYPES = values();
        public final String title;

        MetricType(String title) {
            this.title = title;
        }
    }

    private class GetMetricsTask extends AsyncTask<Void, Void, ListViewAdapter> {
        @Override
        protected ListViewAdapter doInBackground(Void... v) {
            List<Metric> metrics = DBManager.loadFromMetrics().where("Game = ?", mGame.getId()).and("Category = ?", metricCategory).execute();
            ArrayList<ListItem> listMetrics = new ArrayList<ListItem>();
            for (Metric metric : metrics) {
                listMetrics.add(new MetricListElement(metric));
            }
            ListViewAdapter adapter = new ListViewAdapter(MetricsActivity.this, listMetrics);
            return adapter;
        }

        @Override
        protected void onPostExecute(ListViewAdapter adapter) {
            mListView.setAdapter(adapter);
        }
    }
}
