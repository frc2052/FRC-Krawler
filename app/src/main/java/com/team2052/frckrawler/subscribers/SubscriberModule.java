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
