package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.listeners.RefreshListener;

import butterknife.ButterKnife;
import butterknife.Bind;

/**
 * @author Adam
 * @since 11/16/2014
 */
@Deprecated
public abstract class ListFragmentFab extends BaseFragment implements RefreshListener {
    @Bind(R.id.list_layout)
    protected ListView mListView;

    @Bind(R.id.fab)
    protected FloatingActionButton mFab;

    protected ListAdapter mAdapter;
    private Parcelable mListState;
    private boolean mShowAction = true;

    protected void setShowAddAction(boolean state) {
        mShowAction = state;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.list_view_fab, null);
        ButterKnife.bind(this, v);
        preUpdateList();
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            refresh();
        }
        return v;
    }

    public void preUpdateList() {

    }

    @Override
    public void onPause() {
        if (mListView != null) {
            if (mAdapter == null) {
                mAdapter = mListView.getAdapter();
            }
            mListState = mListView.onSaveInstanceState();
        }
        super.onPause();
    }

}
