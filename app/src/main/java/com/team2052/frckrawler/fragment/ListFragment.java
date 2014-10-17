package com.team2052.frckrawler.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ListViewAdapter;
import com.team2052.frckrawler.listeners.ListUpdateListener;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 */
public abstract class ListFragment extends BaseFragment implements ListUpdateListener
{
    @InjectView(R.id.list_layout)
    protected ListView mListView;

    protected ListAdapter mAdapter;
    private Parcelable mListState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.list_view, null);
        ButterKnife.inject(this, view);
        preUpdateList();
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            updateList();
        }
        return view;
    }

    public void preUpdateList()
    {

    }

    @Override
    public void onPause()
    {
        if (mListView != null) {
            if (mAdapter == null) {
                mAdapter = mListView.getAdapter();
            }
            mListState = mListView.onSaveInstanceState();
        }
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        if(mAdapter instanceof ListViewAdapter){
            ((ListViewAdapter) mAdapter).closeLazyList();
        }
        super.onDestroy();
    }
}
