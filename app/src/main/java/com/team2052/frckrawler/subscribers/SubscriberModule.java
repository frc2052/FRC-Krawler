package com.team2052.frckrawler.subscribers;

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

    @Provides
    public MatchListSubscriber provideMatchListSubscriber() {
        return new MatchListSubscriber();
    }

    @Provides
    public RobotListSubscriber provideRobotListSubscriber() {
        return new RobotListSubscriber();
    }

    @Provides
    public EventStringSubscriber provideEventStringListSubscriber() {
        return new EventStringSubscriber();
    }

    @Provides
    public RobotStringSubscriber provideRobotStringListSubscriber() {
        return new RobotStringSubscriber();
    }

    @Provides
    public KeyValueListSubscriber provideKeyMapLIstScubsciber() {
        return new KeyValueListSubscriber();
    }
}
