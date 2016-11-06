package com.team2052.frckrawler.fragments;

import android.os.Bundle;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.database.metric.Compiler;
import com.team2052.frckrawler.subscribers.KeyValueListSubscriber;

import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

public class RobotSummaryFragment extends ListViewFragment<Map<String, String>, KeyValueListSubscriber> {
    @Inject
    Compiler mCompiler;

    Long mRobot_id;

    public static RobotSummaryFragment newInstance(long robot_id) {
        RobotSummaryFragment fragment = new RobotSummaryFragment();
        Bundle args = new Bundle();
        args.putLong(DatabaseActivity.PARENT_ID, robot_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mRobot_id = getArguments().getLong(DatabaseActivity.PARENT_ID);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No metrics found", R.drawable.ic_metric);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends Map<String, String>> getObservable() {
        return mCompiler.getCompiledMetricToHashMap(mCompiler.getCompiledRobotSummary(mRobot_id, null));
    }
}
