package com.team2052.frckrawler.fragments.metric;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.MetricActivity;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.fragments.BaseFragment;
import com.team2052.frckrawler.fragments.metric.dialog.AddMetricDialogFragment;
import com.team2052.frckrawler.listeners.FABButtonListener;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.MetricListElement;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class MetricsFragment extends BaseFragment implements FABButtonListener, RefreshListener {
    private static final String CATEGORY_EXTRA = "CATEGORY_EXTRA";
    private static final String GAME_ID = "GAME_ID";
    @Bind(R.id.list_layout)
    ListView mListView;
    private Game mGame;
    private int mCategory;

    public static MetricsFragment newInstance(int category, Game game) {
        MetricsFragment fragment = new MetricsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(GAME_ID, game.getId());
        bundle.putInt(CATEGORY_EXTRA, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        setHasOptionsMenu(true);
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mListView.setOnItemClickListener((parent, view1, position, id) -> {
            long metric_id = Long.parseLong(((MetricListElement) ((ListViewAdapter) parent.getAdapter()).getItem(position)).getKey());
            Metric metric = mDbManager.getMetricsTable().load(metric_id);
            startActivity(MetricActivity.newInstance(getActivity(), metric));
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGame = mDbManager.getGamesTable().load(getArguments().getLong(GAME_ID, 0));
        mCategory = getArguments().getInt(CATEGORY_EXTRA);
        refresh();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, null);
    }

    @Override
    public void refresh() {
        new GetMetricsTask().execute();
    }

    @Override
    public void onFABPressed() {
        AddMetricDialogFragment.newInstance(mCategory, mGame).show(getChildFragmentManager(), "addMetric");
    }

    private class GetMetricsTask extends AsyncTask<Void, Void, List<Metric>> {
        @Override
        protected List<Metric> doInBackground(Void... v) {
            return mDbManager.getMetricsTable().query(mCategory, null, mGame.getId()).list();
        }

        @Override
        protected void onPostExecute(List<Metric> metrics) {
            ArrayList<ListItem> listMetrics = new ArrayList<>();
            for (Metric metric : metrics) {
                listMetrics.add(new MetricListElement(metric));
            }

            mListView.setAdapter(new ListViewAdapter(getActivity(), listMetrics));
        }
    }

}
