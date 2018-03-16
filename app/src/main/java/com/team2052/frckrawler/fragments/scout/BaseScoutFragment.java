package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.google.firebase.crash.FirebaseCrash;
import com.jakewharton.rxbinding.widget.RxAdapterView;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.helpers.SnackbarHelper;
import com.team2052.frckrawler.interfaces.HasComponent;
import com.team2052.frckrawler.metric.MetricTypes;
import com.team2052.frckrawler.metric.data.MetricValue;
import com.team2052.frckrawler.metric.view.MetricWidget;
import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.Robot;

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
    protected RxDBManager rxDbManager;
    protected Event mEvent;
    protected List<Robot> robots;
    protected List<String> robotNames;
    CompositeSubscription subscriptions = new CompositeSubscription();
    @BindView(R.id.robot)
    Spinner mRobotSpinner;
    @BindView(R.id.comments)
    TextInputLayout mCommentsView;
    @BindView(R.id.button_save)
    View mSaveButton;
    @BindView(R.id.metric_widget_list)
    LinearLayout mMetricList;
    private FragmentComponent mComponent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Database
        if (getActivity() instanceof HasComponent) {
            mComponent = ((HasComponent) getActivity()).getComponent();
        }
        rxDbManager = mComponent.dbManager();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //Bind Views
        ButterKnife.bind(this, view);

        subscriptions.add(RxAdapterView.itemSelections(mRobotSpinner).debounce(500, TimeUnit.MILLISECONDS).subscribe(onNext -> updateMetricValues()));

        mEvent = rxDbManager.getEventsTable().load(getArguments().getLong(EVENT_ID));
        //Load Robots at Event
        rxDbManager.robotsAtEvent(getArguments().getLong(EVENT_ID))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(robots1 -> {
                    this.robots = robots1;
                    return Observable.from(robots1);
                })
                .map(robot -> String.format("%d, %s", robot.getTeam_id(), robot.getTeam().getName()))
                .toList()
                .subscribe(onNext -> {
                    this.robotNames = onNext;
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, onNext);
                    mRobotSpinner.setAdapter(adapter);
                    mRobotSpinner.setSelection(0);
                    updateMetricValues();
                }, onError -> {
                    onError.printStackTrace();
                    FirebaseCrash.report(onError);
                });
        super.onViewCreated(view, savedInstanceState);
    }

    public void updateMetricValues() {

    }

    protected Observable<Robot> robotObservable() {
        return Observable.defer(() -> Observable.just(robots.get(mRobotSpinner.getSelectedItemPosition())));
    }

    protected Robot getSelectedRobot() {
        return robots.get(mRobotSpinner.getSelectedItemPosition());
    }

    public abstract Observable<Boolean> getSaveMetricObservable();

    @OnClick(R.id.button_save)
    protected void saveMetrics(View viewClicked) {
        Subscription saveSubscription = getSaveMetricObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext -> {
                    if (onNext) {
                        SnackbarHelper.make(getView(), "Save Complete", Snackbar.LENGTH_SHORT).show();
                    } else {
                        SnackbarHelper.make(getView(), "Update Complete", Snackbar.LENGTH_SHORT).show();
                    }
                }, onError -> {
                    FirebaseCrash.log("Error Saving Metrics");
                    FirebaseCrash.report(onError);
                    SnackbarHelper.make(getView(), "Cannot Save, make sure you double check everything and try again", Snackbar.LENGTH_SHORT).show();
                });
        subscriptions.add(saveSubscription);
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
                MetricWidget widget = MetricTypes.INSTANCE.createWidget(getActivity(), values.get(i));
                mMetricList.addView(widget);
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
