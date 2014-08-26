package com.team2052.frckrawler.gui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TableLayout;

public class DrawingRequestTableLayout extends TableLayout {

    private boolean refreshRequested;

    public DrawingRequestTableLayout(Context context) {
        super(context);
        refreshRequested = true;
    }

    public DrawingRequestTableLayout(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    protected void onDraw(Canvas c) {
        if (refreshRequested)
            super.onDraw(c);

        refreshRequested = false;
    }

    public void requestDrawingRefresh() {
        refreshRequested = true;
    }
}
