package com.team2052.frckrawler.di;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.subscribers.SubscriberModule;

import dagger.Component;

@Component(modules = {FRCKrawlerModule.class})
public interface ApplicationComponent {
    void inject(FRCKrawler app);

    void inject(SubscriberModule module);

    void inject(FRCKrawlerModule module);
}
