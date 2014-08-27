package com.team2052.frckrawler.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.dialog.AddMetricDialogActivity;
import com.team2052.frckrawler.activity.dialog.EditMetricDialogActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.MetricListElement;

import java.util.ArrayList;

public class MetricsActivity extends DatabaseActivity implements OnClickListener {

    public static final String METRIC_CATEGORY_EXTRA = "com.team2052.frckrawler.metricCategoryExtra";
    public static final int MATCH_PERF_METRICS = 1;
    public static final int ROBOT_METRICS = 2;
    public static final int DRIVER_METRICS = 3;    //Currently not used
    private static final int EDIT_BUTTON_ID = 1;
    private static final int SELECTED_BUTTON_ID = 2;
    private int metricCategory;    //Either MATCH_PERF_METRICS, ROBOT_METRICS, or DRIVER_METRICS
    private int selectedMetricID;
    private DBManager dbManager;
    private Metric[] metrics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metrics);
        metricCategory = getIntent().getIntExtra(METRIC_CATEGORY_EXTRA, -1);
        if (metricCategory == MATCH_PERF_METRICS)
            setTitle("Match Performance Metrics");
        else if (metricCategory == ROBOT_METRICS)
            setTitle("Pit Scout Metrics");
        else if (metricCategory == DRIVER_METRICS)
            setTitle("Driver Metrics");
        else {
            metricCategory = MATCH_PERF_METRICS;
            setTitle("Match Performance Metrics");
        }
        dbManager = DBManager.getInstance(this);
    }

    @Override
    public void onResume() {
        super.onResume();
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
            Intent i = new Intent(this, AddMetricDialogActivity.class);
            i.putExtra(AddMetricDialogActivity.METRIC_CATEGORY_EXTRA, metricCategory);
            i.putExtra(AddMetricDialogActivity.GAME_NAME_EXTRA, databaseValues[getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]);
            startActivity(i);
            return true;
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
            switch (metricCategory) {
                case MATCH_PERF_METRICS:
                    metrics = dbManager.getMatchPerformanceMetricsByColumns(new String[]{DBContract.COL_GAME_NAME}, new String[]{databaseValues[MetricsActivity.this.getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]});
                    break;

                case ROBOT_METRICS:
                    metrics = dbManager.getRobotMetricsByColumns(new String[]{DBContract.COL_GAME_NAME}, new String[]{databaseValues[MetricsActivity.this.getAddressOfDatabaseKey(DBContract.COL_GAME_NAME)]});
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
            ((ListView) findViewById(R.id.metric_list)).setAdapter(adapter);
        }
    }
}
