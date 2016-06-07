package com.team2052.frckrawler.consumer;

import android.app.Activity;
import android.support.annotation.UiThread;
import android.view.View;

public abstract class DataConsumer<V> {
    protected Activity mActivity;
    private OnCompletedListener onCompletedListener;
    protected View rootView;

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

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    @UiThread
    public abstract void bindViews();
}
