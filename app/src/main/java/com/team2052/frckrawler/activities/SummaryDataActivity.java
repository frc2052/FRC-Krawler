package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.MetricsStatsAdapter;
import com.team2052.frckrawler.database.CompiledMetricValue;
import com.team2052.frckrawler.database.MetricCompiler;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.CompiledMetricListElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Adam
 */
public class SummaryDataActivity extends BaseActivity {
    public static String EVENT_ID = "EVENT_ID";
    private ListView mListView;
    private Event mEvent;
    private Metric mMetric;
    private MetricsStatsAdapter mAdapter;

    public static Intent newInstance(Context context, Metric metric, Event event) {
        Intent intent = new Intent(context, SummaryDataActivity.class);
        intent.putExtra(PARENT_ID, metric.getId());
        intent.putExtra(EVENT_ID, event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));
        mListView = (ListView) findViewById(R.id.list_layout);
        mEvent = mDbManager.getEventsTable().load(getIntent().getLongExtra(EVENT_ID, 0));
        mMetric = mDbManager.getMetricsTable().load(getIntent().getLongExtra(PARENT_ID, 0));
        setActionBarTitle(getString(R.string.Summary));
        setActionBarSubtitle(mMetric.getName());
        new GetCompiledData().execute();
    }


    public class GetCompiledData extends AsyncTask<Void, Void, List<CompiledMetricValue>> {

        final float compileWeight;

        public GetCompiledData() {
            this.compileWeight = getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0).getFloat(GlobalValues.PREFS_COMPILE_WEIGHT, 1.0f);

        }

        @Override
        protected List<CompiledMetricValue> doInBackground(Void... params) {
            return MetricCompiler.getCompiledMetric(mEvent, mMetric, mDbManager, compileWeight);
        }

        @Override
        protected void onPostExecute(List<CompiledMetricValue> compiledMetricValues) {
            List<ListItem> listItems = new ArrayList<>();

            Collections.sort(compiledMetricValues, (cmv, cmv1) -> Double.compare(cmv.getRobot().getTeam_id(), cmv1.getRobot().getTeam_id()));

            for (CompiledMetricValue metricValue : compiledMetricValues) {
                listItems.add(new CompiledMetricListElement(metricValue));
            }
            mListView.setAdapter(mAdapter = new MetricsStatsAdapter(SummaryDataActivity.this, mMetric, listItems));
            //mAdapter.getDialog().show();
        }
    }
}
