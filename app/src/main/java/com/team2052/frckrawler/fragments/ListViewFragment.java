package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.items.ListItem;
import com.team2052.frckrawler.di.binding.ListViewBinder;
import com.team2052.frckrawler.di.binding.NoDataParams;
import com.team2052.frckrawler.di.subscribers.BaseDataSubscriber;
import com.team2052.frckrawler.interfaces.RefreshListener;

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
        getBinder().setNoDataParams(getNoDataParams());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        getBinder().setmRootView(view);
    }

    @Override
    public void refresh() {
        getObservable().subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(getSubscriber());
    }

    protected NoDataParams getNoDataParams() {
        return new NoDataParams("No Data Found", R.drawable.ic_no_data);
    }
}
