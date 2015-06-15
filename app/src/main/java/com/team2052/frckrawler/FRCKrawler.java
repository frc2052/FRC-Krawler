package com.team2052.frckrawler;

import android.app.Application;

import com.team2052.frckrawler.database.DBManager;

public class FRCKrawler extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Initiate Database
        DBManager.getInstance(getBaseContext());
    }
}
