package com.team2052.frckrawler.binding;

import android.app.Activity;
import android.support.annotation.UiThread;
import android.view.View;

public abstract class BaseDataBinder<V> {
    protected Activity mActivity;
    protected View rootView;

    public abstract void updateData(V data);

    public void onError(Throwable e) {
    }

    public void setActivity(Activity activity) {
        mActivity = activity;
    }

    public void onCompleted() {
    }

    public void setRootView(View rootView) {
        this.rootView = rootView;
    }

    @UiThread
    public abstract void bindViews();
}
