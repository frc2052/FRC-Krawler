package com.team2052.frckrawler;

import android.app.Application;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.di.ApplicationComponent;
import com.team2052.frckrawler.di.DaggerApplicationComponent;
import com.team2052.frckrawler.di.FRCKrawlerModule;

public class FRCKrawler extends Application {

    private FRCKrawlerModule mModule;
    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        //Initiate Database
        DBManager.getInstance(getBaseContext());
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent
                    .builder()
                    .fRCKrawlerModule(getModule())
                    .build();
        }
        return mApplicationComponent;
    }

    public FRCKrawlerModule getModule() {
        if (mModule == null) {
            mModule = new FRCKrawlerModule(this);
        }
        return mModule;
    }

}
