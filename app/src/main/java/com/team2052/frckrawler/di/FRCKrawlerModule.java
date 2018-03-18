package com.team2052.frckrawler.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.metric.data.RxCompiler;

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
        return RxDBManager.Companion.getInstance(mApp.getApplicationContext());
    }

    @Provides
    @Singleton
    public RxCompiler getDataManager() {
        return new RxCompiler(mApp.getApplicationContext(), getDBManager());
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
