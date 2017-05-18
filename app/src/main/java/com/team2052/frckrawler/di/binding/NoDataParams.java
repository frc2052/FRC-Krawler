package com.team2052.frckrawler.di.binding;

import android.support.annotation.DrawableRes;

public class NoDataParams {
    private String mTitle;
    private int mDrawable;

    public NoDataParams(String mTitle, @DrawableRes int drawableId) {
        this.mTitle = mTitle;
        this.mDrawable = drawableId;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getDrawable() {
        return mDrawable;
    }
}
