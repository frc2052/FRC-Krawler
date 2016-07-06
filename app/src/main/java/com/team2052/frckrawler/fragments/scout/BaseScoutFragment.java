package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.common.base.Optional;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.HasComponent;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.metric.MetricValue;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.util.SnackbarUtil;
import com.team2052.frckrawler.views.metric.MetricWidget;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Adam on 6/15/2016.
 */

public abstract class BaseScoutFragment extends Fragment {
    public static final String EVENT_ID = "EVENT_ID";
    private static final String TAG = "BaseScoutFragment";
    protected DBManager dbManager;
    protected Event mEvent;
    CompositeSubscription subscriptions = new CompositeSubscription();

    @BindView(R.id.robot)
    Spinner mRobotSpinner;

    @BindView(R.id.comments)
    TextInputLayout mCommentsView;

    @BindView(R.id.button_save)
    FloatingActionButton mSaveButton;

    @BindView(R.id.metric_widget_list)
    LinearLayout mMetricList;
    private FragmentComponent mComponent;
    private List<Robot> robots;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Database
        if (getActivity() instanceof HasComponent) {
            mComponent = ((HasComponent) getActivity()).getComponent();
        }
        dbManager = mComponent.dbManager();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Bind Views
        ButterKnife.bind(this, view);

        subscriptions.add(RxAdapterView.itemSelections(mRobotSpinner).debounce(500, TimeUnit.MILLISECONDS).subscribe(onNext -> updateMetricValues()));

        mEvent = dbManager.getEventsTable().load(getArguments().getLong(EVENT_ID));
        //Load Robots at Event
        dbManager.robotsAtEvent(getArguments().getLong(EVENT_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(robots -> this.robots = robots, onError -> {
                }, () -> {
                    Observable.from(robots)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .flatMap(robot -> Observable.just(robot.getTeam_id() + ", " + robot.getTeam().getName()))
                            .toList().subscribe(onNext -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, onNext);
                        mRobotSpinner.setAdapter(adapter);
                        mRobotSpinner.setSelection(0);
                    });
                    updateMetricValues();
                });

        //Get all metrics
        Observable.defer(() -> Observable.just(dbManager.getGamesTable().getMetrics(mEvent.getGame())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::from)
                .filter(this::filterMetric)
                .map(metric -> new MetricValue(metric, null))
                .toList()
                .subscribe(this::setMetricValues);

        super.onViewCreated(view, savedInstanceState);
    }

    public boolean filterMetric(Metric metric) {
        return metric.getEnabled();
    }

    public void updateMetricValues() {

    }

    protected Observable<Robot> robotObservable() {
        return Observable.defer(() -> Observable.just(robots.get(mRobotSpinner.getSelectedItemPosition())));
    }

    public abstract Observable<Boolean> getSaveMetricObservable();

    @OnClick(R.id.button_save)
    protected void saveMetrics(View viewClicked) {
        Subscription saveSubscription = getSaveMetricObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext -> {
                    if (onNext) {
                        SnackbarUtil.make(getView(), "Save Complete", Snackbar.LENGTH_SHORT).show();
                    } else {
                        SnackbarUtil.make(getView(), "Update Complete", Snackbar.LENGTH_SHORT).show();
                    }
                }, onError -> {
                    SnackbarUtil.make(getView(), "Cannot Save, make sure you double check everything and try again", Snackbar.LENGTH_SHORT).show();
                });
        subscriptions.add(saveSubscription);
    }

    /**
     * Please do not use unless you have to
     */
    @Deprecated
    protected Robot getSelectedRobot() {
        return robots.get(mRobotSpinner.getSelectedItemPosition());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
    }

    protected void setMetricValues(List<MetricValue> values) {
        if (values.size() != mMetricList.getChildCount()) {
            //This shouldn't happen, but just in case
            mMetricList.removeAllViews();
            for (int i = 0; i < values.size(); i++) {
                Optional<MetricWidget> widget = MetricWidget.createWidget(getActivity(), values.get(i));
                if (widget.isPresent()) {
                    mMetricList.addView(widget.get());
                }
            }
        } else {
            for (int i = 0; i < values.size(); i++) {
                ((MetricWidget) mMetricList.getChildAt(i)).setMetricValue(values.get(i));
            }
        }
    }

    public List<MetricValue> getValues() {
        List<MetricValue> values = new ArrayList<>();
        for (int i = 0; i < mMetricList.getChildCount(); i++) {
            values.add(((MetricWidget) mMetricList.getChildAt(i)).getValue());
        }
        return values;
    }
}
