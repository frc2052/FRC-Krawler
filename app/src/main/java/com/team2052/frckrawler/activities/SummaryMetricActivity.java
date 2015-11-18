package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.MetricListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 */
public class SummaryMetricActivity extends ListActivity {
    private Event mEvent;

    public static Intent newInstance(Context context, Event event) {
        Intent intent = new Intent(context, SummaryMetricActivity.class);
        intent.putExtra(PARENT_ID, event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = mDbManager.getEventsTable().load(getIntent().getLongExtra(PARENT_ID, 0));
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            Metric metric = mDbManager.getMetricsTable().load(Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey()));
            startActivity(SummaryDataActivity.newInstance(SummaryMetricActivity.this, metric, mEvent));
        });
        if (getActionBar() != null) {
            setActionBarTitle(getString(R.string.Summary));
            setActionBarSubtitle(mEvent.getName());
        }
    }

    @Override
    public void refresh() {
        new LoadAllMetrics().execute();
    }

    public class LoadAllMetrics extends AsyncTask<Void, Void, List<Metric>> {

        @Override
        protected List<Metric> doInBackground(Void... params) {
            return mDbManager.getGamesTable().getMetrics(mDbManager.getEventsTable().getGame(mEvent));
        }

        @Override
        protected void onPostExecute(List<Metric> metrics) {
            List<ListItem> listItems = new ArrayList<>();

            for (Metric metric : metrics) {
                listItems.add(new MetricListElement(metric));
            }

            mListView.setAdapter(mAdapter = new ListViewAdapter(SummaryMetricActivity.this, listItems));
        }
    }


}
