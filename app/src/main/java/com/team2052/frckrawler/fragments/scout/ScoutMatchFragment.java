package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.Lists;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.scout.SaveMatchMetricsTask;
import com.team2052.frckrawler.comparators.RobotTeamNumberComparator;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.database.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.subscribers.BaseScoutData;
import com.team2052.frckrawler.tba.JSON;

import java.util.Collections;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;
import rx.Observable;

/**
 * Created by Adam on 11/26/2015.
 */
public class ScoutMatchFragment extends BaseScoutFragment {
    public static final int MATCH_GAME_TYPE = 0;
    public static final int MATCH_PRACTICE_TYPE = 1;
    private static String MATCH_TYPE = "MATCH_TYPE";

    TextInputLayout mMatchNumber;
    private int mMatchType;

    public static ScoutMatchFragment newInstance(Event event, int type) {
        ScoutMatchFragment scoutMatchFragment = new ScoutMatchFragment();
        Bundle args = new Bundle();
        args.putInt(MATCH_TYPE, type);
        args.putLong(EVENT_ID, event.getId());
        scoutMatchFragment.setArguments(args);
        return scoutMatchFragment;
    }

    @Override
    protected void saveMetrics() {
        //TODO: Handle the errors
        if (!isMatchNumberValid())
            return;
        if (consumer.getSelectedRobot() == null)
            return;
        if (mEvent == null)
            return;

        new SaveMatchMetricsTask(
                getActivity(),
                this,
                mEvent,
                consumer.getSelectedRobot(),
                null,
                getMatchNumber(),
                mMatchType,
                consumer.getValues(),
                consumer.getComment()).execute();
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    public Observable<? extends BaseScoutData> getObservable() {
        return Observable.create(subscriber -> {
            Robot robot = consumer.getSelectedRobot();

            String comment = "";
            List<MetricValue> metricValues = Lists.newArrayList();

            //Get Robots
            List<RobotEvent> robotEvents = dbManager.getEventsTable().getRobotEvents(mEvent);
            List<Robot> robots = Lists.newArrayList();
            for (int i = 0; i < robotEvents.size(); i++) {
                robots.add(robotEvents.get(i).getRobot());
            }
            Collections.sort(robots, new RobotTeamNumberComparator());

            if (robot != null) {
                //Get Metric data
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
                    metricValues.add(new MetricValue(metric, currentData == null ? null : JSON.getAsJsonObject(currentData.getData())));
                }
                final QueryBuilder<MatchComment> matchCommentQueryBuilder
                        = dbManager.getMatchComments().query(match_num, game_type, robot.getId(), mEvent.getId());
                MatchComment mMatchComment = matchCommentQueryBuilder.unique();
                if (mMatchComment != null)
                    comment = mMatchComment.getComment();
            }
            subscriber.onNext(new BaseScoutData(metricValues, robots, comment));
        });
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
        View v = inflater.inflate(R.layout.fragment_scouting_match, null, false);
        consumer.setRootView(v);
        subscriber.bindViewsIfNeeded();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMatchNumber = (TextInputLayout) view.findViewById(R.id.match_number_input);

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
