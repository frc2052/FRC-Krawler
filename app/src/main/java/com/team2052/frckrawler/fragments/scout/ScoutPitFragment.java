package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.scout.SavePitMetricsTask;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.util.SnackbarUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 6/15/2016.
 */

public class ScoutPitFragment extends BaseScoutFragment {
    private static final String TAG = "ScoutPitFragment";

    Observable<List<MetricValue>> metricValueObservable = robotObservable()
            .map(robot -> {
                List<MetricValue> metricValues = Lists.newArrayList();
                final QueryBuilder<Metric> metricQueryBuilder = dbManager.getMetricsTable().query(MetricHelper.ROBOT_METRICS, null, mEvent.getGame_id(), true);
                List<Metric> metrics = metricQueryBuilder.list();

                for (int i = 0; i < metrics.size(); i++) {
                    Metric metric = metrics.get(i);
                    //Query for existing data
                    QueryBuilder<PitData> matchDataQueryBuilder = dbManager.getPitDataTable().query(robot.getId(), metric.getId(), mEvent.getId(), null);

                    PitData currentData = matchDataQueryBuilder.unique();
                    //Add the metric values
                    metricValues.add(new MetricValue(metric, currentData == null ? null : JSON.getAsJsonObject(currentData.getData())));
                }
                return metricValues;
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    Observable<String> metricCommentObservable = robotObservable()
            .flatMap(robot -> Observable.just(Strings.nullToEmpty(robot.getComments())))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    public static ScoutPitFragment newInstance(Event event) {
        Bundle args = new Bundle();
        args.putLong(EVENT_ID, event.getId());
        ScoutPitFragment fragment = new ScoutPitFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting_pit, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public boolean filterMetric(Metric metric) {
        return super.filterMetric(metric) && metric.getCategory() == MetricHelper.ROBOT_METRICS;
    }

    @Override
    public void updateMetricValues() {
        subscriptions.add(metricValueObservable.subscribe(this::setMetricValues, onError -> {
        }));
        subscriptions.add(metricCommentObservable.subscribe(RxTextView.text(mCommentsView.getEditText()), onError -> {
        }));
    }

    @OnClick(R.id.button_save)
    protected void saveMetrics(View clickedView) {
        if (getSelectedRobot() == null || mEvent == null) {
            SnackbarUtil.make(getView(), getActivity().getString(R.string.something_seems_wrong), Snackbar.LENGTH_SHORT).show();
            return;
        }
        new SavePitMetricsTask(
                this,
                mEvent,
                getSelectedRobot(),
                getValues(),
                mCommentsView.getEditText().getText().toString(),
                null).execute();
    }
}
