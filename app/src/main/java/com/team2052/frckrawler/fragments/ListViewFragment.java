package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.consumer.ListViewConsumer;
import com.team2052.frckrawler.database.subscribers.BaseDataSubscriber;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.listitems.ListItem;

import java.util.List;

import rx.schedulers.Schedulers;

public abstract class ListViewFragment<T, S extends BaseDataSubscriber<T, List<ListItem>>>
        extends BaseDataFragment<T, List<ListItem>, S, ListViewConsumer> implements RefreshListener {
    protected ListView mListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mListView = (ListView) view.findViewById(R.id.list_layout);
        binder.listView = mListView;
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void refresh() {
        getObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
    }
}
