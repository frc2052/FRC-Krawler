package com.team2052.frckrawler.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.team2052.frckrawler.listeners.ListUpdateListener;
import com.team2052.frckrawler.R;

/**
 * @author Adam
 */
public abstract class ListFragment extends Fragment implements ListUpdateListener
{
    protected ListView mListView;
    protected ListAdapter mAdapter;
    private Parcelable mListState;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.list_view, null);
        mListView = (ListView) view.findViewById(R.id.list_layout);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            updateList();
        }
        return view;
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
}
