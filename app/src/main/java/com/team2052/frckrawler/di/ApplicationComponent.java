package com.team2052.frckrawler.di;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.database.experiments.DataSubscribersModule;

import dagger.Component;

@Component(modules = {FRCKrawlerModule.class})
public interface ApplicationComponent {
    void inject(FRCKrawler app);

    void inject(DataSubscribersModule module);
}
