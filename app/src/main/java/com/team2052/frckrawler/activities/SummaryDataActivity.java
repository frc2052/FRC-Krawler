package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.MetricsStatsAdapter;
import com.team2052.frckrawler.database.metric.CompiledMetricValue;
import com.team2052.frckrawler.database.metric.MetricCompiler;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.CompiledMetricListElement;
import com.team2052.frckrawler.util.PreferenceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Adam
 */
public class SummaryDataActivity extends DatabaseActivity {
    public static String EVENT_ID = "EVENT_ID";
    private ListView mListView;
    private Event mEvent;
    private Metric mMetric;
    private MetricsStatsAdapter mAdapter;

    public static Intent newInstance(Context context, Metric metric, Event event) {
        Intent intent = new Intent(context, SummaryDataActivity.class);
        intent.putExtra(DatabaseActivity.PARENT_ID, metric.getId());
        intent.putExtra(EVENT_ID, event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);

        mListView = (ListView) findViewById(R.id.list_layout);
        mEvent = dbManager.getEventsTable().load(getIntent().getLongExtra(EVENT_ID, 0));
        mMetric = dbManager.getMetricsTable().load(getIntent().getLongExtra(DatabaseActivity.PARENT_ID, 0));
        setActionBarTitle(getString(R.string.summary_title));
        setActionBarSubtitle(mMetric.getName());
        new GetCompiledData().execute();
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }


    public class GetCompiledData extends AsyncTask<Void, Void, List<CompiledMetricValue>> {
        final float compileWeight;

        public GetCompiledData() {
            this.compileWeight = PreferenceUtil.compileWeight(SummaryDataActivity.this);
        }

        @Override
        protected List<CompiledMetricValue> doInBackground(Void... params) {
            return MetricCompiler.getCompiledMetric(mEvent, mMetric, dbManager, compileWeight);
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
