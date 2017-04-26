package com.team2052.frckrawler.views;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.theme.Themes;

/**
 * @author Adam
 * @since 10/25/2014
 */
public class NavDrawerImageView extends AppCompatImageView {

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
        ColorStateList list = ResourcesCompat.getColorStateList(getResources(),
                Themes.getCurrentTheme(getContext()).isLight() ? R.color.nav_selected_image_color_light : R.color.nav_selected_image_color
                , getContext().getTheme());
        int color = list.getColorForState(getDrawableState(), Color.TRANSPARENT);
        setColorFilter(color);
        invalidate();
    }
}
