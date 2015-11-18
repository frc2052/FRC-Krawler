package com.team2052.frckrawler.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.melnykov.fab.FloatingActionButton;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.listeners.RefreshListener;

/**
 * @author Adam
 */
@Deprecated
public abstract class ListActivity extends BaseActivity implements RefreshListener {
    protected ListView mListView;
    protected ListAdapter mAdapter;
    private Parcelable mListState;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);
        mListView = (ListView) findViewById(R.id.list_layout);
    }

    public FloatingActionButton getFab() {
        return mFab;
    }

    @Override
    protected void onPause() {
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
    protected void onResume() {
        if (mAdapter != null) {
            mListView.setAdapter(mAdapter);
            mListView.onRestoreInstanceState(mListState);
        } else {
            refresh();
        }
        super.onResume();
    }
}
