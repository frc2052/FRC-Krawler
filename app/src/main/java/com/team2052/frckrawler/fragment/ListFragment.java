package com.team2052.frckrawler.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.team2052.frckrawler.ListUpdateListener;
import com.team2052.frckrawler.R;

/**
 * @author Adam
 */
public abstract class ListFragment extends Fragment implements ListUpdateListener {
    protected ListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, null);
        mListView = (ListView) view.findViewById(R.id.list_layout);
        updateList();
        return view;
    }
}
