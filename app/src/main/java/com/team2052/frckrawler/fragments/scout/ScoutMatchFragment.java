package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.common.base.Optional;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.scout.SaveMatchMetricsTask;
import com.team2052.frckrawler.consumer.OnCompletedListener;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.tba.JSON;
import com.team2052.frckrawler.views.metric.MetricWidget;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 11/26/2015.
 */
public class ScoutMatchFragment extends BaseScoutFragment implements OnCompletedListener, View.OnClickListener {
    public static final int MATCH_GAME_TYPE = 0;
    public static final int MATCH_PRACTICE_TYPE = 1;
    private static String MATCH_TYPE = "MATCH_TYPE";

    TextInputLayout mComments, mMatchNumber;
    LinearLayout mMetricList;
    private int mMatchType;

    public Observable<MatchScoutData> matchScoutDataObservable(Robot robot) {
        return Observable.just(robot).map(robot1 -> {
            MatchScoutData matchScoutData = new MatchScoutData();
            final int match_num = getMatchNumber();
            final int game_type = mMatchType;
            final QueryBuilder<Metric> metricQueryBuilder = dbManager.getMetricsTable().query(MetricHelper.MATCH_PERF_METRICS, null, mEvent.getGame_id());
            List<Metric> metrics = metricQueryBuilder.list();
            for (int i = 0; i < metrics.size(); i++) {
                Metric metric = metrics.get(i);
                //Query for existing data
                QueryBuilder<MatchData> matchDataQueryBuilder = dbManager.getMatchDataTable().query(robot.getId(), metric.getId(), match_num, game_type, mEvent.getId(), null);
                MatchData currentData = matchDataQueryBuilder.unique();
                //Add the metric values
                matchScoutData.values.add(new MetricValue(metric, currentData == null ? null : JSON.getAsJsonObject(currentData.getData())));
            }
            final QueryBuilder<MatchComment> matchCommentQueryBuilder
                    = dbManager.getMatchComments().query(match_num, game_type, robot.getId(), mEvent.getId());
            MatchComment mMatchComment = matchCommentQueryBuilder.unique();
            if (mMatchComment != null)
                matchScoutData.comments = mMatchComment.getComment();
            return matchScoutData;
        });
    }

    public Subscriber<MatchScoutData> matchScoutDataObserver() {
        return new Subscriber<MatchScoutData>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onStart() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(MatchScoutData matchScoutData) {
                if (mComments.getEditText() != null)
                    mComments.getEditText().setText(matchScoutData.comments);
                setData(matchScoutData.values);
                onCompleted();
            }
        };
    }

    @Override
    protected void saveMetrics() {
        new SaveMatchMetricsTask(
                getActivity(),
                this,
                mEvent,
                getRobot(),
                null,
                getMatchNumber(),
                mMatchType,
                getValues(),
                mComments.getEditText().getText().toString()).execute();
    }

    public List<MetricValue> getValues() {
        List<MetricValue> values = new ArrayList<>();
        for (int i = 0; i < mMetricList.getChildCount(); i++) {
            values.add(((MetricWidget) mMetricList.getChildAt(i)).getValue());
        }
        return values;
    }

    public static class MatchScoutData {
        public String comments = "";
        public List<MetricValue> values = new ArrayList<>();
    }

    public static ScoutMatchFragment newInstance(Event event, int type) {
        ScoutMatchFragment scoutMatchFragment = new ScoutMatchFragment();
        Bundle args = new Bundle();
        args.putInt(MATCH_TYPE, type);
        args.putLong(EVENT_ID, event.getId());
        scoutMatchFragment.setArguments(args);
        return scoutMatchFragment;
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMatchType = getArguments().getInt(MATCH_TYPE);
        scoutType = MetricHelper.MATCH_PERF_METRICS;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scouting_match, null, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mComments = (TextInputLayout) view.findViewById(R.id.comments);
        mMatchNumber = (TextInputLayout) view.findViewById(R.id.match_number_input);
        mMetricList = (LinearLayout) view.findViewById(R.id.metric_widget_list);

        if (mMatchNumber.getEditText() != null)
            mMatchNumber.getEditText().addTextChangedListener(new TextWatcher() {
                boolean init = false;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!init) {
                        init = true;
                        return;
                    }
                    try {
                        int value = Integer.parseInt(s.toString());
                        mMatchNumber.setErrorEnabled(false);
                        mMatchNumber.setError("");
                        updateMetricList();
                    } catch (NumberFormatException e) {
                        mMatchNumber.setErrorEnabled(true);
                        mMatchNumber.setError("Invalid Number");
                    }

                }
            });
    }

    public void updateMetricList() {
        if (isSelectedRobotValid() && isMatchNumberValid())
            matchScoutDataObservable(getRobot())
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(matchScoutDataObserver());
    }

    private int getMatchNumber() {
        try {
            return Integer.parseInt(mMatchNumber.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isMatchNumberValid() {
        try {
            Integer.parseInt(mMatchNumber.getEditText().getText().toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void setData(List<MetricValue> metricValues) {
        if (metricValues.size() != mMetricList.getChildCount()) {
            //This shouldn't happen, but just in case
            for (int i = 0; i < metricValues.size(); i++) {
                Optional<MetricWidget> widget = MetricWidget.createWidget(getActivity(), metricValues.get(i));
                if (widget.isPresent())
                    mMetricList.addView(widget.get());
            }
        } else {
            for (int i = 0; i < metricValues.size(); i++) {
                ((MetricWidget) mMetricList.getChildAt(i)).setMetricValue(metricValues.get(i));
            }
        }
    }
}
