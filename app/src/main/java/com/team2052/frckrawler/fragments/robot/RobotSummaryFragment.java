package com.team2052.frckrawler.fragments.robot;

import android.os.Bundle;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.DatabaseActivity;
import com.team2052.frckrawler.adapters.items.smart.metric.BooleanMetricValueSummaryCard;
import com.team2052.frckrawler.adapters.items.smart.metric.MetricSummaryCardMultiMapper;
import com.team2052.frckrawler.adapters.items.smart.metric.NumberMetricSummaryCard;
import com.team2052.frckrawler.di.binding.NoDataParams;
import com.team2052.frckrawler.di.binding.RecyclerViewBinder;
import com.team2052.frckrawler.fragments.RecyclerViewFragment;
import com.team2052.frckrawler.metric.data.CompiledMetricValue;
import com.team2052.frckrawler.metric.data.Compiler;

import java.util.List;

import javax.inject.Inject;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public class RobotSummaryFragment extends RecyclerViewFragment<List<CompiledMetricValue>, RecyclerViewBinder> {
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
    protected NoDataParams getNoDataParams() {
        return new NoDataParams("No metrics found", R.drawable.ic_metric);
    }

    @Override
    public void inject() {
        mComponent.inject(this);
    }

    @Override
    protected Observable<? extends List<CompiledMetricValue>> getObservable() {
        return mCompiler.getCompiledRobotSummary(
                mRobot_id,
                null,
                Observable.just(mRobot_id)
                        .map(rxDbManager.getRobotsTable()::load)
                        .concatMap(robot -> rxDbManager.metricsInGame(robot.getGame_id(), null))
        );
    }

    @Override
    public void provideAdapterCreator(SmartAdapter.MultiAdaptersCreator creator) {
        creator.map(CompiledMetricValue.class, BooleanMetricValueSummaryCard.class)
                .map(CompiledMetricValue.class, NumberMetricSummaryCard.class)
                .builder(new MetricSummaryCardMultiMapper());
    }

    @Override
    protected boolean showDividers() {
        return false;
    }
}
