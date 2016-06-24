package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.team2052.frckrawler.activities.HasComponent;
import com.team2052.frckrawler.binding.BaseDataBinder;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.subscribers.BaseDataSubscriber;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by Acorp on 11/17/2015.
 * T - Type of the Data
 * V - View Type of Data
 * S - Subscriber Type
 * B - Binder Type
 */
public abstract class BaseDataFragment
        <T, V, S extends BaseDataSubscriber<T, V>, B extends BaseDataBinder<V>>
        extends Fragment {
    protected FragmentComponent mComponent;
    protected DBManager dbManager;
    @Inject
    protected S subscriber;
    @Inject
    protected B binder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasComponent) {
            mComponent = ((HasComponent) getActivity()).getComponent();
        }
        inject();
        dbManager = mComponent.dbManager();
        subscriber.setConsumer(binder);
        binder.setActivity(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        getObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
    }

    public abstract void inject();

    protected abstract Observable<? extends T> getObservable();
}
