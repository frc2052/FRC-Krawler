package com.team2052.frckrawler;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import frckrawler.DaoMaster;
import frckrawler.DaoSession;

/**
 * @author Adam
 */
//Used to initiate ActiveAndroid Database
//Also can be used for normal Application methods Note: Make sure you call your supers to initiate the DB
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
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "FRCKrawler-GreenDao", null);
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
