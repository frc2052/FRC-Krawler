package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.AddMetricActivity;
import com.team2052.frckrawler.activities.MetricInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listitems.elements.MetricListElement;
import com.team2052.frckrawler.subscribers.MetricListSubscriber;

import java.util.List;

import rx.Observable;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class MetricsFragment extends ListViewFragment<List<Metric>, MetricListSubscriber> implements FABButtonListener {
    private static final String CATEGORY_EXTRA = "CATEGORY_EXTRA";
    private static final String GAME_ID = "GAME_ID";
    private long mGame_id;
    private int mCategory;

    public static MetricsFragment newInstance(int category, long game_id) {
        MetricsFragment fragment = new MetricsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(GAME_ID, game_id);
        bundle.putInt(CATEGORY_EXTRA, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            long metric_id = Long.parseLong(((MetricListElement) ((ListViewAdapter) parent.getAdapter()).getItem(position)).getKey());
            Metric metric = dbManager.getMetricsTable().load(metric_id);
            startActivity(MetricInfoActivity.newInstance(getActivity(), metric));
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame_id = getArguments().getLong(GAME_ID, 0);
        mCategory = getArguments().getInt(CATEGORY_EXTRA);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Metric>> getObservable() {
        return dbManager.metricsInGame(mGame_id, mCategory);
    }

    @Override
    public void onFABPressed() {
        startActivity(AddMetricActivity.newInstance(getActivity(), mGame_id, mCategory));
        //AddMetricDialogFragment.newInstance(mCategory, mGame_id).show(getChildFragmentManager(), "addMetric");
    }

    @Override
    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No metrics found", R.drawable.ic_metric);
    }
}
