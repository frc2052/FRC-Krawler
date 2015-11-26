package com.team2052.frckrawler.di;

import android.content.Context;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.bluetooth.client.ScoutSyncHandler;
import com.team2052.frckrawler.database.DBManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class FRCKrawlerModule {
    static FRCKrawler mApp;

    public FRCKrawlerModule() {
    }

    public FRCKrawlerModule(FRCKrawler app) {
        mApp = app;
    }

    @Provides
    public Context provideApplicationContext() {
        return mApp.getApplicationContext();
    }

    @Provides
    @Singleton
    public DBManager getDBManager() {
        return DBManager.getInstance(mApp.getApplicationContext());
    }

    @Provides
    @Singleton
    public ScoutSyncHandler getScoutSyncHander() {
        return ScoutSyncHandler.getInstance(mApp.getApplicationContext());
    }
}
