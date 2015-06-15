package com.team2052.frckrawler.activities;

import android.os.Bundle;

import com.team2052.frckrawler.database.DBManager;

/**
 * @author Adam
 */
public class BaseActivity extends NavigationDrawerActivity {
    public static final String PARENT_ID = "PARENT_ID";

    protected DBManager mDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDbManager = DBManager.getInstance(this);
        super.onCreate(savedInstanceState);
    }
}
