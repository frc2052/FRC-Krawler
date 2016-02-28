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

import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 11/26/2015.
 */
public class ScoutMatchFragment extends BaseScoutFragment {
    public static final int MATCH_GAME_TYPE = 0;
    public static final int MATCH_PRACTICE_TYPE = 1;
    private static String MATCH_TYPE = "MATCH_TYPE";

    TextInputLayout mMatchNumber;
    LinearLayout mMetricList;
    private int mMatchType;

    public static ScoutMatchFragment newInstance(Event event, int type) {
        ScoutMatchFragment scoutMatchFragment = new ScoutMatchFragment();
        Bundle args = new Bundle();
        args.putInt(MATCH_TYPE, type);
        args.putLong(EVENT_ID, event.getId());
        scoutMatchFragment.setArguments(args);
        return scoutMatchFragment;
    }

    public Observable<ScoutData> matchScoutDataObservable(Robot robot) {
        return Observable.just(robot).map(robot1 -> {
            ScoutData matchScoutData = new ScoutData();
            final int match_num = getMatchNumber();
            final int game_type = mMatchType;
            final QueryBuilder<Metric> metricQueryBuilder = dbManager.getMetricsTable().query(scoutType, null, mEvent.getGame_id(), true);
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

    @Override
    protected void saveMetrics() {
        //TODO: Handle the errors
        if (!isMatchNumberValid())
            return;
        if (getRobot() == null)
            return;
        if (mEvent == null)
            return;
        if (mComments.getEditText() == null)
            return;

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
                        Integer.parseInt(s.toString());
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
                    .subscribe(scoutDataSubscriber());
    }

    private int getMatchNumber() {
        try {
            return Integer.parseInt(mMatchNumber.getEditText().getText().toString());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private boolean isMatchNumberValid() {
        return getMatchNumber() != -1;
    }
}
