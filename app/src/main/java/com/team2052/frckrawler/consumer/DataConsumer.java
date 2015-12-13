package com.team2052.frckrawler.consumer;

import android.app.Activity;

public abstract class DataConsumer<V> {
    protected Activity mActivity;
    private OnCompletedListener onCompletedListener;

    public abstract void updateData(V data);

    public void onError(Throwable e) {
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void onCompleted() {
        if (onCompletedListener != null)
            onCompletedListener.onCompleted();
    }

    public void setOnCompletedListener(OnCompletedListener onCompletedListener) {
        this.onCompletedListener = onCompletedListener;
    }
}
