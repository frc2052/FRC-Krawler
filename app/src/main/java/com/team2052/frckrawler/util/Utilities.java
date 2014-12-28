package com.team2052.frckrawler.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

/**
 * @author Adam
 * @since 12/27/14.
 */
public class Utilities {
    public static int getPixelsFromDp(Context c, int dipValue) {
        Resources r = c.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
}
