package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.binding.ListViewBinder;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.listeners.RefreshListener;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.subscribers.BaseDataSubscriber;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.schedulers.Schedulers;

public abstract class ListViewFragment<T, S extends BaseDataSubscriber<T, List<ListItem>>>
        extends BaseDataFragment<T, List<ListItem>, S, ListViewBinder> implements RefreshListener {
    @BindView(R.id.list)
    protected ListView mListView;

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
        ButterKnife.bind(this, view);
        binder.setmRootView(view);
    }

    @Override
    public void refresh() {
        getObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(subscriber);
    }

    protected ListViewNoDataParams getNoDataParams() {
        return new ListViewNoDataParams("No Data Found", R.drawable.ic_no_data);
    }
}
