package com.team2052.frckrawler.helpers.metric;

import android.support.annotation.IntDef;

import com.google.gson.JsonElement;
import com.team2052.frckrawler.data.tba.v3.JSON;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MetricHelper {
    public static final int MATCH_PERF_METRICS = 0, ROBOT_METRICS = 1;
    public static final int BOOLEAN = 0,
            COUNTER = 1,
            SLIDER = 2,
            CHOOSER = 3,
            CHECK_BOX = 4,
            STOP_WATCH = 5,
            TEXT_FIELD = 6;
    public static final int MINIMUM_DEFAULT_VALUE = 1;
    public static final int MAXIMUM_DEFAULT_VALUE = 10;
    public static final int INCREMENTATION_DEFAULT_VALUE = 1;
    public static final int MATCH_GAME_TYPE = 0;
    public static final int MATCH_PRACTICE_TYPE = 1;

    public static String convertToJsonString(JsonElement json) {
        return JSON.getGson().toJson(json);
    }

    @IntDef({BOOLEAN, COUNTER, SLIDER, CHOOSER, CHECK_BOX, STOP_WATCH, TEXT_FIELD})
    public @interface MetricType {
    }

    @IntDef({MATCH_PERF_METRICS, ROBOT_METRICS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MetricCategory {
    }
}
