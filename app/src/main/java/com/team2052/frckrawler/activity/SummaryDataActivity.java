package com.team2052.frckrawler.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.CompiledMetricValue;
import com.team2052.frckrawler.database.MetricCompiler;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.SimpleListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class SummaryDataActivity extends ListActivity {
    public static String EVENT_ID = "EVENT_ID";
    private Event mEvent;
    private Metric mMetric;

    public static Intent newInstance(Context context, Metric metric, Event event) {
        Intent intent = new Intent(context, SummaryDataActivity.class);
        intent.putExtra(PARENT_ID, metric.getId());
        intent.putExtra(EVENT_ID, event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mEvent = Event.load(Event.class, getIntent().getLongExtra(EVENT_ID, 0));
        mMetric = Metric.load(Metric.class, getIntent().getLongExtra(PARENT_ID, 0));
        if (getActionBar() != null) {
            getActionBar().setTitle(mMetric.name + " - Summary");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void updateList() {
        new GetCompiledData().execute();
    }


    public class GetCompiledData extends AsyncTask<Void, Void, List<CompiledMetricValue>> {

        @Override
        protected List<CompiledMetricValue> doInBackground(Void... params) {
            return MetricCompiler.compileMetricDataInEvent(mEvent, mMetric);
        }

        @Override
        protected void onPostExecute(List<CompiledMetricValue> compiledMetricValues) {
            List<ListItem> listItems = new ArrayList<>();
            for (CompiledMetricValue metricValue : compiledMetricValues) {
                listItems.add(new SimpleListElement(metricValue.robot.team.number + " - " + metricValue.compiledValue, Integer.toString(metricValue.robot.team.number)));
            }
            mListView.setAdapter(mAdapter = new ListViewAdapter(SummaryDataActivity.this, listItems));
        }
    }
}
