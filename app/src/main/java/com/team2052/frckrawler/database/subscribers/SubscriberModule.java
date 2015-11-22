package com.team2052.frckrawler.database.subscribers;

import android.app.Activity;

import com.team2052.frckrawler.di.FRCKrawlerModule;

import dagger.Module;
import dagger.Provides;

@Module(includes = {FRCKrawlerModule.class})
public class SubscriberModule {
    private Activity mActivity;

    public SubscriberModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    public EventListSubscriber provideEventListSubscriber() {
        return new EventListSubscriber();
    }

    @Provides
    public GameListSubscriber provideGameListSubscriber() {
        return new GameListSubscriber();
    }

    @Provides
    public MetricListSubscriber provideMetricListSubscriber() {
        return new MetricListSubscriber();
    }

    @Provides
    public TeamListSubscriber provideTeamListSubscriber() {
        return new TeamListSubscriber();
    }
}
