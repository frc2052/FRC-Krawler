package com.team2052.frckrawler.core.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.activities.DatabaseActivity;
import com.team2052.frckrawler.core.adapters.ListViewAdapter;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.core.fragments.dialog.AddMetricFragment;
import com.team2052.frckrawler.core.listeners.FABButtonListener;
import com.team2052.frckrawler.core.listitems.ListElement;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.elements.MetricListElement;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * @author Adam
 * @since 10/15/2014
 */
public class MetricsFragment extends ListFragment implements FABButtonListener {
    public static String CATEGORY = "CATEGORY";
    public ActionMode mCurrentActionMode;
    private final ActionMode.Callback callback = new ActionMode.Callback() {

        Metric metric = null;

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            long metricId = Long.parseLong(((ListElement) mAdapter.getItem(mCurrentSelectedItem)).getKey());
            metric = mDaoSession.getMetricDao().load(metricId);
            actionMode.getMenuInflater().inflate(R.menu.edit_delete_menu, menu);
            menu.removeItem(R.id.menu_edit);
            actionMode.setTitle(metric.getName());
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.menu_delete) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Are you sure you want to remove this metric and all its data?");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDaoSession.runInTx(new Runnable() {
                            @Override
                            public void run() {
                                DBManager.getInstance(getActivity(), mDaoSession).deleteMetric(metric);
                            }
                        });
                        dialogInterface.dismiss();
                        updateList();
                        actionMode.finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mCurrentActionMode = null;
        }
    };
    private int mCurrentSelectedItem;
    private Game mGame;
    private int mCategory;

    public static MetricsFragment newInstance(int category, Game game) {
        MetricsFragment fragment = new MetricsFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(DatabaseActivity.PARENT_ID, game.getId());
        bundle.putInt(CATEGORY, category);
        fragment.setArguments(bundle);
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        setHasOptionsMenu(true);
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mCurrentActionMode != null) {
                    return false;
                }
                mCurrentSelectedItem = i;
                mCurrentActionMode = ((ActionBarActivity) getActivity()).startSupportActionMode(callback);
                return true;
            }
        });
        return view;
    }

    @Override
    public void preUpdateList() {
        mGame = mDaoSession.getGameDao().load(getArguments().getLong(DatabaseActivity.PARENT_ID, 0));
        mCategory = getArguments().getInt(CATEGORY);
    }

    @Override
    public void updateList() {
        new GetMetricsTask().execute();
    }

    @Override
    public void onFABPressed() {
        AddMetricFragment.newInstance(mCategory, mGame).show(getChildFragmentManager(), "addMetric");
    }

    private class GetMetricsTask extends AsyncTask<Void, Void, List<Metric>> {
        @Override
        protected List<Metric> doInBackground(Void... v) {
            QueryBuilder<Metric> metricQueryBuilder = mDaoSession.getMetricDao().queryBuilder();
            metricQueryBuilder.where(MetricDao.Properties.Category.eq(mCategory));
            metricQueryBuilder.where(MetricDao.Properties.GameId.eq(mGame.getId()));
            ;
            return metricQueryBuilder.list();
        }

        @Override
        protected void onPostExecute(List<Metric> metrics) {
            if (metrics.size() == 0) {
                showError(true);
                return;
            }
            showError(false);
            ArrayList<ListItem> listMetrics = new ArrayList<>();
            for (Metric metric : metrics) {
                listMetrics.add(new MetricListElement(metric));
            }

            mListView.setAdapter(mAdapter = new ListViewAdapter(getActivity(), listMetrics));
        }
    }

}
