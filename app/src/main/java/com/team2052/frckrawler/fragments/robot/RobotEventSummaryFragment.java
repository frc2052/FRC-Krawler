package com.team2052.frckrawler.fragments.robot;

import android.os.Bundle;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.di.binding.NoDataParams;
import com.team2052.frckrawler.di.subscribers.KeyValueListSubscriber;
import com.team2052.frckrawler.fragments.ListViewFragment;
import com.team2052.frckrawler.metric.data.Compiler;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

public class RobotEventSummaryFragment extends ListViewFragment<Map<String, String>, KeyValueListSubscriber> {
    public static final String EVENT_ID = "EVENT_ID";
    @Inject
    Compiler mCompiler;
    private long mRobot_id;
    private long mEvent_id;

    public static RobotEventSummaryFragment newInstance(long robot_id, long event_id) {
        RobotEventSummaryFragment fragment = new RobotEventSummaryFragment();
        Bundle args = new Bundle();
        args.putLong(DatabaseActivity.PARENT_ID, robot_id);
        args.putLong(EVENT_ID, event_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mRobot_id = getArguments().getLong(DatabaseActivity.PARENT_ID);
        mEvent_id = getArguments().getLong(EVENT_ID);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected NoDataParams getNoDataParams() {
        return new NoDataParams("No metrics found", R.drawable.ic_metric);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends Map<String, String>> getObservable() {
        return mCompiler.getCompiledMetricToHashMap(mCompiler.getCompiledRobotSummary(mRobot_id, mEvent_id,
                Observable.just(mRobot_id)
                        .map(rxDbManager.getRobotsTable()::load)
                        .concatMap(robot -> rxDbManager.metricsInGame(robot.getGame_id(), null))));
    }
}
