package com.team2052.frckrawler.core.activities;

import android.os.Bundle;

import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;

/**
 * @author Adam
 */
public class DatabaseActivity extends BaseActivity {
    public static final String PARENT_ID = "PARENT_ID";
    protected DBManager mDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDbManager = ((FRCKrawler) getApplication()).getDBSession();
        super.onCreate(savedInstanceState);
    }
}
