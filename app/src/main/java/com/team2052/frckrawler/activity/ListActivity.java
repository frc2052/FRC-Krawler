package com.team2052.frckrawler.activity;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;

/**
 * @author Adam
 */
public abstract class ListActivity extends DatabaseActivity implements ListUpdateListener
{
    protected ListView mListView;
    protected ListAdapter mAdapter;
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        mListView = (ListView) findViewById(R.id.list_layout);
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            updateList();
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (mListView != null) {
            //Set the adapter if we are too lazy to set the variable
            if (mAdapter == null) {
                mAdapter = mListView.getAdapter();
            }
            mListState = mListView.onSaveInstanceState();
        }
    }
}
