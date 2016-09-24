package com.team2052.frckrawler.binding;

import android.support.annotation.DrawableRes;

/**
 * Created by Adam on 9/1/2016.
 */
public class ListViewNoDataParams {
    private String mTitle;
    private int mDrawable;

    public ListViewNoDataParams(String mTitle, @DrawableRes int drawableId) {
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
