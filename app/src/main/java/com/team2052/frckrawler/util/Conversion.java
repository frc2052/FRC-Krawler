package com.team2052.frckrawler.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

public class Conversion {

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}
