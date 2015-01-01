package com.team2052.frckrawler.core.fragments;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.activities.SummaryDataActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.MetricListElement;
import com.team2052.frckrawler.core.util.LogHelper;

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
        bundle.putLong(DatabaseActivity.PARENT_ID, event.getId());
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void preUpdateList() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Metric metric = mDaoSession.getMetricDao().load(Long.parseLong(((ListElement) parent.getAdapter().getItem(position)).getKey()));
                startActivity(SummaryDataActivity.newInstance(getActivity(), metric, mEvent));
            }
        });
        mEvent = mDaoSession.getEventDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
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
            return mDaoSession.getMetricDao().queryBuilder().where(MetricDao.Properties.GameId.eq(mEvent.getGameId())).list();
        }

        @Override
        protected void onPostExecute(List<Metric> metrics) {
            LogHelper.debug(String.valueOf(metrics.size()));
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
