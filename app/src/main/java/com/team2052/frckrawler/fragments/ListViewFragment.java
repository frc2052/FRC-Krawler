package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.subscribers.BaseDataSubscriber;

import java.util.List;

import rx.schedulers.Schedulers;

public abstract class ListViewFragment<T, S extends BaseDataSubscriber<T, List<ListItem>>>
        extends BaseDataFragment<T, List<ListItem>, S, ListViewBinder> implements RefreshListener {
    protected ListView mListView;
    protected ImageView mNoDataImage;
    protected TextView mNoDataTitle;
    protected View mNoDataRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_view, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binder.setNoDataParams(getNoDataParams());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(R.id.list_layout);
        mNoDataImage = (ImageView) view.findViewById(R.id.no_data_image);
        mNoDataTitle = (TextView) view.findViewById(R.id.no_data_title);
        mNoDataRootView = view.findViewById(R.id.no_data_root_view);

        binder.listView = mListView;
        binder.noDataImage = mNoDataImage;
        binder.noDataTitle = mNoDataTitle;
        binder.noDataRootView = mNoDataRootView;
    }

    @Override
    public void refresh() {
        getObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
    }

    protected ListViewBinder.ListViewNoDataParams getNoDataParams() {
        return new ListViewBinder.ListViewNoDataParams("No Data Found", R.drawable.ic_no_data);
    }
}
