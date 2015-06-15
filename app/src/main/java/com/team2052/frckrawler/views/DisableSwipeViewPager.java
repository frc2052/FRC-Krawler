package com.team2052.frckrawler.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * A pretty simple implementation of a view pager to disable swiping
 */
public class DisableSwipeViewPager extends ViewPager {

    private boolean swipeEnabled = false;

    public DisableSwipeViewPager(Context context) {
        super(context);
    }

    public DisableSwipeViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return swipeEnabled && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!swipeEnabled) {
            return false;
        }
        return super.onTouchEvent(event);
    }

    public void setEnableSwipe(boolean enabled) {
        swipeEnabled = enabled;
    }

    public boolean goToNextPage() {
        if (getCurrentItem() < getAdapter().getCount() - 1) {
            setCurrentItem(getCurrentItem() + 1);
            return true;
        }
        return false;
    }

    public boolean goToPreviousPage() {
        if (getCurrentItem() > 0) {
            setCurrentItem(getCurrentItem() - 1);
            return true;
        }
        return false;
    }
}