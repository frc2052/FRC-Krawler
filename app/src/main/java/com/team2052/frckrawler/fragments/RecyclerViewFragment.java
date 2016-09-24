package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.DividerItemDecoration;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.binding.ListViewNoDataParams;
import com.team2052.frckrawler.binding.RecyclerViewBinder;
import com.team2052.frckrawler.subscribers.BaseDataSubscriber;

import java.util.List;

import io.nlopez.smartadapters.SmartAdapter;
import rx.Observable;

public abstract class RecyclerViewFragment<T, S extends BaseDataSubscriber<T, List<Object>>, B extends RecyclerViewBinder> extends BaseDataFragment<T, List<Object>, S, B> implements RecyclerViewBinder.RecyclerViewAdapterCreatorProvider {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

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

    protected abstract ListViewNoDataParams getNoDataParams();

    protected boolean showDividers(){
        return true;
    }
}
