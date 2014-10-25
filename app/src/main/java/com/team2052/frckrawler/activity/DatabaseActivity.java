package com.team2052.frckrawler.activity;

import android.os.Bundle;

import com.team2052.frckrawler.FRCKrawler;

import frckrawler.DaoSession;

/**
 * @author Adam
 */
public class DatabaseActivity extends BaseActivity
{
    public static final String PARENT_ID = "PARENT_ID";
    protected DaoSession mDaoSession;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mDaoSession = ((FRCKrawler) getApplication()).getDaoSession();
        super.onCreate(savedInstanceState);
    }
}
