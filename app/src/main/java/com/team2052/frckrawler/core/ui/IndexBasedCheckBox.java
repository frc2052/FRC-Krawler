package com.team2052.frckrawler.core.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by adam on 3/28/15.
 */
public class IndexBasedCheckBox extends CheckBox {
    private int index;

    public IndexBasedCheckBox(Context context) {
        super(context);
    }

    public IndexBasedCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IndexBasedCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IndexBasedCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
