package com.team2052.frckrawler.activity;

import android.content.*;
import android.os.*;
import android.view.View;
import android.widget.AdapterView;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.database.models.*;
import com.team2052.frckrawler.listitems.*;

import java.util.*;

/**
 * @author Adam
 */
public class SummaryMetricsActivity extends ListActivity
{
    private Event mEvent;

    public static Intent newInstance(Context context, Event event)
    {
        Intent intent = new Intent(context, SummaryMetricsActivity.class);
        intent.putExtra(PARENT_ID, event.getId());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mEvent = Event.load(Event.class, getIntent().getLongExtra(PARENT_ID, 0));
        super.onCreate(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Metric metric = Metric.load(Metric.class, Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey()));
                startActivity(SummaryDataActivity.newInstance(SummaryMetricsActivity.this, metric, mEvent));
            }
        });
        if (getActionBar() != null) {
            getActionBar().setTitle("Summary - " + mEvent.name);
        }
    }

    @Override
    public void updateList()
    {
        new LoadAllMetrics().execute();
    }

    public class LoadAllMetrics extends AsyncTask<Void, Void, List<Metric>>
    {

        @Override
        protected List<Metric> doInBackground(Void... params)
        {
            return new Select().from(Metric.class).where("Game = ?", mEvent.game.getId()).execute();
        }

        @Override
        protected void onPostExecute(List<Metric> metrics)
        {
            List<ListItem> listItems = new ArrayList<>();

            for (Metric metric : metrics) {
                listItems.add(new MetricListElement(metric));
            }

            mListView.setAdapter(mAdapter = new ListViewAdapter(SummaryMetricsActivity.this, listItems));
        }
    }


}
