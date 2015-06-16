package com.team2052.frckrawler.fragments.metric;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.team2052.frckrawler.activities.BaseActivity;
import com.team2052.frckrawler.activities.SummaryDataActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.fragments.ListFragment;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.MetricListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adam
 * @since 10/16/2014
 */
public class SummaryFragment extends ListFragment {
    private Event mEvent;

    public static SummaryFragment newInstance(Event event) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(BaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void preUpdateList() {
        mListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Metric metric = mDbManager.getMetricsTable().load(Long.parseLong(((ListElement) adapterView.getAdapter().getItem(i)).getKey()));
            startActivity(SummaryDataActivity.newInstance(getActivity(), metric, mEvent));
        });
        mEvent = mDbManager.getEventsTable().load(getArguments().getLong(BaseActivity.PARENT_ID));
    }

    @Override
    public void updateList() {
        new LoadAllMetrics().execute();
    }


    @Override
    public void onAttach(Activity activity) {
        setShowAddAction(false);
        super.onAttach(activity);
    }

    public class LoadAllMetrics extends AsyncTask<Void, Void, List<Metric>> {

        @Override
        protected List<Metric> doInBackground(Void... params) {
            return mDbManager.getMetricsTable().query(null, null, mEvent.getGame_id()).list();
        }

        @Override
        protected void onPostExecute(List<Metric> metrics) {
            if (metrics.size() == 0) {
                showError(true);
                return;
            }
            showError(false);
            List<ListItem> listItems = new ArrayList<>();

            for (Metric metric : metrics) {
                listItems.add(new MetricListElement(metric));
            }

            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), listItems));
        }
    }
}
