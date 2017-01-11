package com.team2052.frckrawler;

import android.app.Application;

import com.team2052.frckrawler.binding.BinderModule;
import com.team2052.frckrawler.di.ApplicationComponent;
import com.team2052.frckrawler.di.DaggerApplicationComponent;
import com.team2052.frckrawler.di.FRCKrawlerModule;
import com.team2052.frckrawler.metric.MetricTypeEntryHandler;

public class FRCKrawler extends Application {
    private FRCKrawlerModule mModule;
    private ApplicationComponent mApplicationComponent;
    private BinderModule mBinderModule;

    @Override
    public void onCreate() {
        MetricTypeEntryHandler.INSTANCE.init();
        super.onCreate();
    }

    public ApplicationComponent getComponent() {
        if (mApplicationComponent == null) {
            mApplicationComponent = DaggerApplicationComponent
                    .builder()
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

    public BinderModule getConsumerModule() {
        if (mBinderModule == null) {
            mBinderModule = new BinderModule();
        }
        return mBinderModule;
    }
}
