package com.team2052.frckrawler.core.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.db.DaoSession;

/**
 * @author Adam
 * @since 10/5/2014
 */
public class BaseFragment extends Fragment {
    protected DaoSession mDaoSession;
    protected DBManager mDBManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mDaoSession = ((FRCKrawler) getActivity().getApplication()).getDaoSession();
        mDBManager = DBManager.getInstance(getActivity(), mDaoSession);
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
