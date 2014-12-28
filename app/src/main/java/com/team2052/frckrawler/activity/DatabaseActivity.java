package com.team2052.frckrawler.activity;

import android.os.Bundle;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.DaoSession;

/**
 * @author Adam
 */
public class DatabaseActivity extends BaseActivity {
    public static final String PARENT_ID = "PARENT_ID";
    protected DaoSession mDaoSession;
    protected DBManager mDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDaoSession = ((FRCKrawler) getApplication()).getDaoSession();
        mDBManager = DBManager.getInstance(this, mDaoSession);
        super.onCreate(savedInstanceState);
    }
}
