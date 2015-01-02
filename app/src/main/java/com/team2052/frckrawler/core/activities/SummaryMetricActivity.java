package com.team2052.frckrawler.core.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.MetricListElement;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;

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
        mEvent = mDaoSession.getEventDao().load(getIntent().getLongExtra(PARENT_ID, 0));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Metric metric = mDaoSession.getMetricDao().load(Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey()));
                startActivity(SummaryDataActivity.newInstance(SummaryMetricActivity.this, metric, mEvent));
            }
        });
        if (getActionBar() != null) {
            setActionBarTitle(getString(R.string.Summary));
            setActionBarSubtitle(mEvent.getName());
        }
    }

    @Override
    public void updateList() {
        new LoadAllMetrics().execute();
    }

    public class LoadAllMetrics extends AsyncTask<Void, Void, List<Metric>> {

        @Override
        protected List<Metric> doInBackground(Void... params) {
            return mDaoSession.getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(mEvent.getGame().getId())).list();
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
