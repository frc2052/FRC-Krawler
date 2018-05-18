package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.DividerItemDecoration;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.data.models.RxDBManager;
import com.team2052.frckrawler.di.FragmentComponent;
import com.team2052.frckrawler.di.binding.NoDataParams;
import com.team2052.frckrawler.di.binding.RecyclerViewBinder;
import com.team2052.frckrawler.di.subscribers.BaseDataSubscriber;
import com.team2052.frckrawler.interfaces.HasComponent;
import com.team2052.frckrawler.interfaces.RefreshListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.schedulers.Schedulers;

public abstract class RecyclerViewFragment<T extends List, B extends RecyclerViewBinder> extends Fragment implements RecyclerViewBinder.RecyclerViewAdapterCreatorProvider, RefreshListener {

    protected FragmentComponent mComponent;
    protected RxDBManager rxDbManager;
    @Inject
    protected B binder;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private BaseDataSubscriber<T, List<Object>> subscriber;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof HasComponent) {
            mComponent = ((HasComponent) getActivity()).getComponent();
        }

        subscriber = new BaseDataSubscriber<T, List<Object>>() {
            @Override
            public void parseData() {
                dataToBind = new ArrayList<>(data);
            }
        };
        inject();
        rxDbManager = mComponent.dbManager();
        subscriber.setConsumer(binder);
        binder.setActivity(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binder.setmRootView(view);
        binder.setRecyclerViewAdapterCreatorProvider(this);
        binder.setNoDataParams(getNoDataParams());

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list);
        mRecyclerView.setHasFixedSize(true);

        if (showDividers()) {
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        }

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        super.onViewCreated(view, savedInstanceState);
    }

    protected NoDataParams getNoDataParams() {
        return new NoDataParams("No Data Found", R.drawable.ic_no_data);
    }

    protected boolean showDividers() {
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public void refresh() {
        getObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
    }

    public abstract void inject();

    protected abstract Observable<? extends T> getObservable();
}
