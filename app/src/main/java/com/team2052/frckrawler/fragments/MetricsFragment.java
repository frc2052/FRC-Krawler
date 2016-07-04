package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.AddMetricActivity;
import com.team2052.frckrawler.activities.ImportMetricsActivity;
import com.team2052.frckrawler.activities.MetricInfoActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.database.MetricHelper;
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

        if (mCategory == MetricHelper.MATCH_PERF_METRICS) {
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.metric_import_firebase, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.import_metrics_menu){
            startActivity(ImportMetricsActivity.newInstance(getContext(), mGame_id, mCategory));
        }
        return super.onOptionsItemSelected(item);
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
    }

    @Override
    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No metrics found", R.drawable.ic_metric);
    }
}
