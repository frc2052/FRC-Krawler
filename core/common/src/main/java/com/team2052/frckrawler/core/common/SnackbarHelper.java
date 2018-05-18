package com.team2052.frckrawler.core.common;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Adam on 10/9/2015.
 */
public class SnackbarHelper {
    public static Snackbar makeSnackbarTextWhite(Snackbar snackbar) {
        View view = snackbar.getView();
        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        return snackbar;
    }

    public static Snackbar make(View view, CharSequence text, int duration) {
        return makeSnackbarTextWhite(Snackbar.make(view, text, duration));
    }
}
