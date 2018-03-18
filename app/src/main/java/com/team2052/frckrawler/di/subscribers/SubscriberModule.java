package com.team2052.frckrawler.di.subscribers;

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
    public KeyValueListSubscriber provideKeyMapListSubbscriber() {
        return new KeyValueListSubscriber();
    }

    @Provides
    StringListSubscriber provideStringListSubscriber() {
        return new StringListSubscriber();
    }
}
