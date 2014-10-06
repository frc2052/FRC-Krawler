package com.team2052.frckrawler.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.team2052.frckrawler.FRCKrawler;

import frckrawler.DaoSession;

/**
 * @author Adam
 * @since 10/5/2014
 */
public class BaseFragment extends Fragment
{
    protected DaoSession mDaoSession;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mDaoSession = ((FRCKrawler) getActivity().getApplication()).getDaoSession();
        onPostCreate();
        super.onCreate(savedInstanceState);
    }


    public void onPostCreate()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
