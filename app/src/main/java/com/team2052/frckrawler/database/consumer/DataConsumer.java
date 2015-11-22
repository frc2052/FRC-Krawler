package com.team2052.frckrawler.database.consumer;

import android.app.Activity;

public abstract class DataConsumer<V> {
    protected Activity mActivity;

    public abstract void updateData(V data);

    public abstract void onError(Throwable e);

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void onCompleted() {
    }
}
