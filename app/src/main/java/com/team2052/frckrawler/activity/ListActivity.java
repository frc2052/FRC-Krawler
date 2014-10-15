package com.team2052.frckrawler.activity;

import android.os.Bundle;
import android.os.Parcelable;
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
public abstract class ListActivity extends DatabaseActivity implements ListUpdateListener
{
    @InjectView(R.id.list_layout) protected ListView mListView;
    protected ListAdapter mAdapter;
    private Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume()
    {
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            updateList();
        }
        super.onResume();
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

    @Override
    protected void onDestroy()
    {
        if(mAdapter instanceof ListViewAdapter){
            ((ListViewAdapter) mAdapter).closeLazyList();
        }
        super.onDestroy();
    }
}
