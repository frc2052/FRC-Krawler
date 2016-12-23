package com.team2052.frckrawler.views;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;

public class LockableFloatingActionButton extends FloatingActionButton {
    boolean locked = false;

    public LockableFloatingActionButton(Context context) {
        super(context);
    }

    public LockableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LockableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    @Override
    public void hide() {
        if(locked)
            return;
        super.hide();
    }

    @Override
    public void show() {
        if (locked)
            return;
        super.show();
    }
}
