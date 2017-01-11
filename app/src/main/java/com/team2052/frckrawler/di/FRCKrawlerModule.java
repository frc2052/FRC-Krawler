package com.team2052.frckrawler.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.bluetooth.client.ScoutSyncHandler;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.database.metric.Compiler;

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
    public RxDBManager getDBManager() {
        return RxDBManager.getInstance(mApp.getApplicationContext());
    }

    @Provides
    @Singleton
    public Compiler getDataManager() {
        return new Compiler(mApp.getApplicationContext(), getDBManager());
    }

    @Provides
    @Singleton
    public ScoutSyncHandler getScoutSyncHandler(Context context) {
        return new ScoutSyncHandler(context);
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
