package com.team2052.frckrawler.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.adapters.items.smart.MetricItemView;
import com.team2052.frckrawler.adapters.items.smart.SmartAdapterInteractions;
import com.team2052.frckrawler.di.binding.NoDataParams;
import com.team2052.frckrawler.di.binding.RecyclerViewBinder;
import com.team2052.frckrawler.core.data.models.Metric;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 *
 */
public class SummaryFragment extends RecyclerViewFragment<List<Metric>, RecyclerViewBinder> {
    public static SummaryFragment newInstance(long event_id) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.Companion.getPARENT_ID(), event_id);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Metric>> getObservable() {
        return null;// rxDbManager.metricsByCategory(mEvent.getSeason_id(), null);
    }

    @Override
    protected NoDataParams getNoDataParams() {
        return new NoDataParams("No metrics found", R.drawable.ic_metric);
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(Metric.class, MetricItemView.class);
        creator.listener((actionId, item, position, view) -> {
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item instanceof Metric) {
                Metric metric = (Metric) item;
                //startActivity(SummaryDataActivity.newInstance(getActivity(), metric.getId(), mEvent.getId()));
            }
        });
    }
}
