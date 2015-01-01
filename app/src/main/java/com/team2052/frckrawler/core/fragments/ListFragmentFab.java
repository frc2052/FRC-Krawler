package com.team2052.frckrawler.core.fragments;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.listeners.ListUpdateListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 * @since 11/16/2014
 */
public abstract class ListFragmentFab extends BaseFragment implements ListUpdateListener {
    @InjectView(R.id.list_layout)
    protected ListView mListView;

    @InjectView(R.id.fab)
    protected ImageButton mFab;

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
        ButterKnife.inject(this, v);
        preUpdateList();
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            updateList();
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
