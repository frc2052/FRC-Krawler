package com.team2052.frckrawler.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.database.DBManager;

/**
 * @author Adam
 * @since 10/5/2014
 */
public class BaseFragment extends Fragment {
    protected DBManager mDbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDbManager = DBManager.getInstance(getActivity());
        onPostCreate();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void onPostCreate() {
    }
}
