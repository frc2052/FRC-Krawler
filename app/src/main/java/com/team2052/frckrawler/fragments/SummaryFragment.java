package com.team2052.frckrawler.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.activities.SummaryDataActivity;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.binding.RecyclerViewBinder;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listitems.smart.MetricItemView;
import com.team2052.frckrawler.listitems.smart.SmartAdapterInteractions;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * @author Adam
 * @since 10/16/2014
 */
public class SummaryFragment extends RecyclerViewFragment<List<Metric>, RecyclerViewBinder> {
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
        mEvent = rxDbManager.getEventsTable().load(getArguments().getLong(DatabaseActivity.PARENT_ID));
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Metric>> getObservable() {
        return rxDbManager.metricsInGame(mEvent.getGame_id(), null);
    }

    @Override
    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No metrics found", R.drawable.ic_metric);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Metric.class, MetricItemView.class);
        creator.listener((actionId, item, position, view) -> {
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item instanceof Metric) {
                Metric metric = (Metric) item;
                startActivity(SummaryDataActivity.newInstance(getActivity(), metric.getId(), mEvent.getId()));
            }
        });
    }
}
