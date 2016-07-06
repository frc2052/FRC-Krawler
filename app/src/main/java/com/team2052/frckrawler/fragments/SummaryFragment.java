package com.team2052.frckrawler.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.SummaryDataActivity;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listitems.ListElement;
import com.team2052.frckrawler.subscribers.MetricListSubscriber;

import java.util.List;

import rx.Observable;

/**
 * @author Adam
 * @since 10/16/2014
 */
public class SummaryFragment extends ListViewFragment<List<Metric>, MetricListSubscriber> {
    private Event mEvent;

    public static SummaryFragment newInstance(long event_id) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, event_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEvent = dbManager.getEventsTable().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
        mListView.setOnItemClickListener((adapterView, view1, i, l) -> {
            Metric metric = dbManager.getMetricsTable().load(Long.parseLong(((ListElement) adapterView.getAdapter().getItem(i)).getKey()));
            startActivity(SummaryDataActivity.newInstance(getActivity(), metric, mEvent));
        });
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Metric>> getObservable() {
        return dbManager.metricsInGame(mEvent.getGame_id(), null);
    }

    @Override
    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No metrics found", R.drawable.ic_metric);
    }
}
