package com.team2052.frckrawler.di;

import android.app.Application;

import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.experiments.DatabaseObserver;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        FRCKrawlerModule.class
}, dependencies = ApplicationComponent.class)
public interface FragmentComponent {
    DBManager dbManager();
    void inject(GameInfoActivity gameInfoActivity);
}
