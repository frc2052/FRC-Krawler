package com.team2052.frckrawler;

import android.support.multidex.MultiDexApplication;

import com.team2052.frckrawler.di.ApplicationComponent;
import com.team2052.frckrawler.di.DaggerApplicationComponent;
import com.team2052.frckrawler.di.FRCKrawlerModule;
import com.team2052.frckrawler.di.binding.BinderModule;

public class FRCKrawler extends MultiDexApplication {
    private FRCKrawlerModule mModule;
    private ApplicationComponent mApplicationComponent;
    private BinderModule mBinderModule;

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
