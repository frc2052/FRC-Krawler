package com.team2052.frckrawler.fragments.scout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activities.HasComponent;
import com.team2052.frckrawler.consumer.BaseScoutDataConsumer;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.MetricHelper;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.subscribers.BaseScoutData;
import com.team2052.frckrawler.subscribers.BaseScoutSubscriber;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Adam on 11/26/2015.
 */
public abstract class BaseScoutFragment extends Fragment implements View.OnClickListener {
    public static final String EVENT_ID = "EVENT_ID";

    @MetricHelper.MetricCategory
    public int scoutType = 0;
    protected Event mEvent;

    @Inject
    protected BaseScoutSubscriber subscriber;
    @Inject
    protected BaseScoutDataConsumer consumer;
    protected DBManager dbManager;
    protected FragmentComponent mComponent;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        consumer.mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMetricList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        view.findViewById(R.id.button_save).setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasComponent) {
            mComponent = ((HasComponent) getActivity()).getComponent();
        }
        inject();
        dbManager = mComponent.dbManager();

        subscriber.setConsumer(consumer);
        consumer.setActivity(getActivity());

        mEvent = dbManager.getEventsTable().load(getArguments().getLong(EVENT_ID));
    }

    protected abstract void inject();

    public abstract Observable<? extends BaseScoutData> getObservable();

    @Override
    public void onResume() {
        super.onResume();
        updateMetricList();
    }

    protected void updateMetricList() {
        getObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save) {
            saveMetrics();
        }
    }

    protected abstract void saveMetrics();
}
