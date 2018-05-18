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
import com.team2052.frckrawler.core.common.SnackbarHelper;
import com.team2052.frckrawler.core.data.models.RxDBManager;
import com.team2052.frckrawler.core.data.models.Team;
import com.team2052.frckrawler.core.metrics.MetricTypes;
import com.team2052.frckrawler.core.metrics.data.MetricValue;
import com.team2052.frckrawler.core.metrics.view.MetricWidget;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.interfaces.HasComponent;

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
    protected List<Team> teams;
    protected List<String> robotNames;

    CompositeSubscription subscriptions = new CompositeSubscription();

    @BindView(R.id.team)
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

        //Load Robots at Event
        rxDbManager.getTeamsTable().loadAllObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(teams1 -> teams = teams1)
                .concatMap(Observable::from)
                .map(robot -> String.format("%d, %s", robot.getNumber(), robot.getName()))
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

    protected Observable<Team> teamObservable() {
        return Observable.defer(() -> Observable.just(teams.get(mRobotSpinner.getSelectedItemPosition())));
    }

    protected Team getSelectedTeam() {
        return teams.get(mRobotSpinner.getSelectedItemPosition());
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
