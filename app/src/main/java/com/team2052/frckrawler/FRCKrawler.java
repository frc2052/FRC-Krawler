package com.team2052.frckrawler;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.team2052.frckrawler.database.DatabaseHelper;

import frckrawler.DaoMaster;
import frckrawler.DaoSession;

/**
 * @author Adam
 */
public class FRCKrawler extends Application
{

    private DaoSession daoSession;

    @Override
    public void onCreate()
    {
        super.onCreate();
        setupDB();
    }

    private void setupDB()
    {
        DatabaseHelper helper = new DatabaseHelper(this, "FRCKrawler", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession()
    {
        return daoSession;
    }

    public static DaoSession getDaoSession(Context context)
    {
        return ((FRCKrawler) context.getApplicationContext()).getDaoSession();
    }

}
