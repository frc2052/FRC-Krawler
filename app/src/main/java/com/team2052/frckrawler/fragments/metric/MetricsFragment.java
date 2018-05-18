package com.team2052.frckrawler.fragments.metric;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.AddMetricActivity;
import com.team2052.frckrawler.activities.ImportMetricsActivity;
import com.team2052.frckrawler.activities.MetricInfoActivity;
import com.team2052.frckrawler.adapters.items.smart.MetricItemView;
import com.team2052.frckrawler.adapters.items.smart.SmartAdapterInteractions;
import com.team2052.frckrawler.core.common.MetricHelper;
import com.team2052.frckrawler.di.binding.NoDataParams;
import com.team2052.frckrawler.di.binding.RecyclerViewBinder;
import com.team2052.frckrawler.fragments.RecyclerViewFragment;
import com.team2052.frckrawler.core.data.models.Metric;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class MetricsFragment extends RecyclerViewFragment<List<Metric>, RecyclerViewBinder> implements View.OnClickListener {
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
        if (item.getItemId() == R.id.import_metrics_menu) {
            startActivity(ImportMetricsActivity.Companion.newInstance(getContext(), mGame_id, mCategory));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<Metric>> getObservable() {
        return rxDbManager.metricsByCategory(mCategory);
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
                startActivity(MetricInfoActivity.Companion.newInstance(getActivity(), metric.getId()));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floating_action_button) {
            startActivity(AddMetricActivity.newInstance(getActivity(), mGame_id, mCategory));
        }
    }
}
