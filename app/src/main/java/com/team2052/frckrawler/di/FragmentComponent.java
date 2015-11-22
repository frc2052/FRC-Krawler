package com.team2052.frckrawler.di;

import com.team2052.frckrawler.activities.EventInfoActivity;
import com.team2052.frckrawler.activities.GameInfoActivity;
import com.team2052.frckrawler.activities.HomeActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.consumer.ConsumerModule;
import com.team2052.frckrawler.database.subscribers.SubscriberModule;
import com.team2052.frckrawler.fragments.EventsFragment;
import com.team2052.frckrawler.fragments.GamesFragment;
import com.team2052.frckrawler.fragments.MetricsFragment;
import com.team2052.frckrawler.fragments.TeamsFragment;

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

    void inject(HomeActivity activity);

    void inject(GamesFragment gamesFragment);

    void inject(GameInfoActivity gameInfoActivity);

    void inject(EventsFragment eventsNewFragment);

    void inject(EventInfoActivity eventInfoActivity);

    void inject(MetricsFragment metricsFragment);

    void inject(TeamsFragment teamsFragment);
}
