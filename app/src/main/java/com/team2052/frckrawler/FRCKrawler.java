package com.team2052.frckrawler;

import android.app.Application;

import com.team2052.frckrawler.database.consumer.ConsumerModule;
import com.team2052.frckrawler.di.ApplicationComponent;
import com.team2052.frckrawler.di.DaggerApplicationComponent;
import com.team2052.frckrawler.di.FRCKrawlerModule;

public class FRCKrawler extends Application {
    private FRCKrawlerModule mModule;
    private ApplicationComponent mApplicationComponent;
    private ConsumerModule mConsumerModule;

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent
                    .builder()
                    .fRCKrawlerModule(getModule())
                    .build();
        }
        return mApplicationComponent;
    }

    public FRCKrawlerModule getModule() {
        if (mModule == null) {
            mModule = new FRCKrawlerModule(this);
        }
        return mModule;
    }

    public ConsumerModule getConsumerModule() {
        if (mConsumerModule == null) {
            mConsumerModule = new ConsumerModule();
        }
        return mConsumerModule;
    }
}
