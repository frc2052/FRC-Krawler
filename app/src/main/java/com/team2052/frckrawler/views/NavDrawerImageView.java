package com.team2052.frckrawler.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.team2052.frckrawler.R;

/**
 * @author Adam
 * @since 10/25/2014
 */
public class NavDrawerImageView extends ImageView {

    public NavDrawerImageView(Context context) {
        super(context);
    }

    public NavDrawerImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NavDrawerImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        ColorStateList list = getResources().getColorStateList(R.color.nav_selected_image_color);
        int color = list.getColorForState(getDrawableState(), Color.TRANSPARENT);
        setColorFilter(color);
        invalidate();
    }
}
