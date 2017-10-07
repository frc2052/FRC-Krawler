package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.data.tba.v3.JSON;
import com.team2052.frckrawler.helpers.metric.MetricHelper;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.Metric;
import com.team2052.frckrawler.models.MetricDao;
import com.team2052.frckrawler.models.PitDatum;
import com.team2052.frckrawler.models.Robot;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ScoutPitFragment extends BaseScoutFragment {
    private static final String TAG = "ScoutPitFragment";
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    Observable<List<MetricValue>> metricValueObservable = robotObservable()
            .map(robot -> {
                List<MetricValue> metricValues = Lists.newArrayList();
                final QueryBuilder<Metric> metricQueryBuilder = rxDbManager.getMetricsTable().query(MetricHelper.ROBOT_METRICS, null, mEvent.getSeason_id(), true)
                        .orderDesc(MetricDao.Properties.Priority)
                        .orderAsc(MetricDao.Properties.Id);
                List<Metric> metrics = metricQueryBuilder.list();

                for (int i = 0; i < metrics.size(); i++) {
                    Metric metric = metrics.get(i);
                    //Query for existing data
                    QueryBuilder<PitDatum> matchDataQueryBuilder = rxDbManager.getPitDataTable().query(robot.getId(), metric.getId(), mEvent.getId());

                    PitDatum currentData = matchDataQueryBuilder.unique();
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
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        supportActionBar.setDisplayHomeAsUpEnabled(true);
        supportActionBar.setTitle(R.string.pit_scout);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void updateMetricValues() {
        subscriptions.add(metricValueObservable.subscribe(this::setMetricValues, onError -> {
            //Most likely part of the robot observable not being initiated, no big deal
            if (onError instanceof ArrayIndexOutOfBoundsException) {
                return;
            }
            FirebaseCrash.log("Pit: Error Updating Metric Values");
            FirebaseCrash.report(onError);
        }));
        subscriptions.add(metricCommentObservable.subscribe(RxTextView.text(mCommentsView.getEditText()), onError -> {
            //Most likely part of the robot observable not being initiated, no big deal
            if (onError instanceof ArrayIndexOutOfBoundsException) {
                return;
            }
            FirebaseCrash.log("Pit: Error Updating Comment");
            FirebaseCrash.report(onError);
        }));
    }

    @Override
    public Observable<Boolean> getSaveMetricObservable() {
        return Observable.combineLatest(
                robotObservable(),
                Observable.defer(() -> Observable.just(getValues())),
                Observable.just(mCommentsView.getEditText().getText().toString()),
                PitScoutSaveMetric::new)
                .map(pitScoutSaveMetric -> {
                    boolean saved = false;
                    Robot robot = pitScoutSaveMetric.robot;
                    for (MetricValue widget : pitScoutSaveMetric.metricValues) {
                        PitDatum pitDatum = new PitDatum(null);
                        pitDatum.setRobot(robot);
                        pitDatum.setMetric(widget.getMetric());
                        pitDatum.setEvent(mEvent);
                        //pitDatum.setUser_id(user != null ? user.getId() : null); NOOP
                        pitDatum.setData(widget.valueAsString());
                        if (rxDbManager.getPitDataTable().insertWithSaved(pitDatum) && !saved)
                            saved = true;
                    }

                    robot.setComments(pitScoutSaveMetric.comment);
                    robot.setLast_updated(new Date());
                    robot.update();

                    return saved;
                });
    }

    public static class PitScoutSaveMetric {
        Robot robot;
        List<MetricValue> metricValues;
        private String comment;

        public PitScoutSaveMetric(Robot robot, List<MetricValue> metricValues, String comment) {
            this.robot = robot;
            this.metricValues = metricValues;
            this.comment = comment;
        }
    }
}
