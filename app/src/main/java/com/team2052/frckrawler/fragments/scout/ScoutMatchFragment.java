package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.scout.SaveMatchMetricsTask;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.tba.JSON;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ScoutMatchFragment extends BaseScoutFragment {
    private static final String TAG = "ScoutMatchFragment";
    public static final int MATCH_GAME_TYPE = 0;
    public static final int MATCH_PRACTICE_TYPE = 1;
    private static String MATCH_TYPE = "MATCH_TYPE";
    private int mMatchType;

    @BindView(R.id.match_number_input)
    TextInputLayout mMatchNumberInput;

    private static class MetricValueUpdateParams {
        Integer match_num;
        Robot robot;

        public MetricValueUpdateParams(Integer match_num, Robot robot) {
            this.match_num = match_num;
            this.robot = robot;
        }
    }

    Observable<List<MetricValue>> metricValueObservable = Observable
            .combineLatest(matchNumberObservable(), robotObservable(), (match_num, robot) -> new MetricValueUpdateParams(match_num, robot))
            .map(valueParams -> {
                List<MetricValue> metricValues = Lists.newArrayList();
                final QueryBuilder<Metric> metricQueryBuilder = dbManager.getMetricsTable().query(MetricHelper.MATCH_PERF_METRICS, null, mEvent.getGame_id(), true);
                List<Metric> metrics = metricQueryBuilder.list();
                for (int i = 0; i < metrics.size(); i++) {
                    Metric metric = metrics.get(i);
                    //Query for existing data
                    QueryBuilder<MatchData> matchDataQueryBuilder = dbManager.getMatchDataTable().query(valueParams.robot.getId(), metric.getId(), valueParams.match_num, mMatchType, mEvent.getId(), null);
                    MatchData currentData = matchDataQueryBuilder.unique();
                    //Add the metric values
                    metricValues.add(new MetricValue(metric, currentData == null ? null : JSON.getAsJsonObject(currentData.getData())));
                }
                return metricValues;
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    Observable<String> metricCommentObservable = Observable
            .combineLatest(matchNumberObservable(), robotObservable(), (match_num, robot) -> new MetricValueUpdateParams(match_num, robot))
            .map(valueParams -> {
                final QueryBuilder<MatchComment> matchCommentQueryBuilder
                        = dbManager.getMatchComments().query(valueParams.match_num, mMatchType, valueParams.robot.getId(), mEvent.getId());
                MatchComment mMatchComment = matchCommentQueryBuilder.unique();
                String comment = null;
                if (mMatchComment != null)
                    comment = mMatchComment.getComment();
                return Strings.nullToEmpty(comment);
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    public static ScoutMatchFragment newInstance(Event event, int type) {
        ScoutMatchFragment scoutMatchFragment = new ScoutMatchFragment();
        Bundle args = new Bundle();
        args.putInt(MATCH_TYPE, type);
        args.putLong(EVENT_ID, event.getId());
        scoutMatchFragment.setArguments(args);
        return scoutMatchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMatchType = getArguments().getInt(MATCH_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting_match, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.bind(this, view);

        subscriptions.add(RxTextView.afterTextChangeEvents(mMatchNumberInput.getEditText())
                .filter(event -> {
                    try {
                        Integer.parseInt(event.editable().toString());
                        mMatchNumberInput.setErrorEnabled(false);
                        mMatchNumberInput.setError("");
                    } catch (NumberFormatException e1) {
                        mMatchNumberInput.setErrorEnabled(true);
                        mMatchNumberInput.setError("Invalid Number");
                        return false;
                    }
                    return true;
                }).debounce(500, TimeUnit.MILLISECONDS).subscribe(onNext -> updateMetricValues()));
    }

    @Override
    public boolean filterMetric(Metric metric) {
        return super.filterMetric(metric) && metric.getCategory() == MetricHelper.MATCH_PERF_METRICS;
    }

    private Observable<Integer> matchNumberObservable() {
        return Observable.defer(() -> Observable.just(Integer.parseInt(mMatchNumberInput.getEditText().getText().toString())));
    }

    @Override
    public void updateMetricValues() {
        subscriptions.add(metricValueObservable.subscribe(this::setMetricValues, onError -> {
        }));
        subscriptions.add(metricCommentObservable.subscribe(RxTextView.text(mCommentsView.getEditText()), onError -> {
        }));
    }


    @OnClick(R.id.button_save)
    protected void saveMetrics(View viewClicked) {
        //TODO: Handle the errors
        if (!isMatchNumberValid())
            return;
        if (getSelectedRobot() == null)
            return;
        if (mEvent == null)
            return;

        new SaveMatchMetricsTask(
                getActivity(),
                this,
                mEvent,
                getSelectedRobot(),
                null,
                getMatchNumber(),
                mMatchType,
                getValues(),
                mCommentsView.getEditText().getText().toString()).execute();
    }

    @Deprecated
    private int getMatchNumber() {
        try {
            return Integer.parseInt(mMatchNumberInput.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Deprecated
    private boolean isMatchNumberValid() {
        return getMatchNumber() != -1;
    }
}
