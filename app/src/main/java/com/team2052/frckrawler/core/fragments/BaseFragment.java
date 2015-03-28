package com.team2052.frckrawler.core.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;

/**
 * @author Adam
 * @since 10/5/2014
 */
public class BaseFragment extends Fragment {
    protected DBManager mDbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDbManager = ((FRCKrawler) getActivity().getApplication()).getDBSession();
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
