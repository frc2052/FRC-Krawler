package com.team2052.frckrawler.di;

import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.consumer.ConsumerModule;
import com.team2052.frckrawler.database.subscribers.SubscriberModule;
import com.team2052.frckrawler.fragments.event.EventsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {
        FRCKrawlerModule.class,
        SubscriberModule.class,
        ConsumerModule.class,
}, dependencies = ApplicationComponent.class)
public interface FragmentComponent {
    DBManager dbManager();

    void inject(GameInfoActivity gameInfoActivity);

    void inject(EventsFragment eventsNewFragment);
}
