package com.team2052.frckrawler.core.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
    private static final String
            COMPILE_TEAM_NAME_PREFERENCE = "compile_team_name_preference",
            COMPILE_MATCH_METRICS_PREFERENCE = "compile_match_metrics_preference",
            COMPILE_PIT_METRICS_PREFERENCE = "compile_pit_metric_preference",
            COMPILE_WEIGHT_PREFERENCE = "compile_weight_preference";

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static boolean compileTeamNamesToExport(Context context) {
        return getBooleanPreference(context, COMPILE_TEAM_NAME_PREFERENCE, true);
    }

    public static boolean compileMatchMetricsToExport(Context context) {
        return getBooleanPreference(context, COMPILE_MATCH_METRICS_PREFERENCE, true);
    }

    public static boolean compilePitMetricsToExport(Context context) {
        return getBooleanPreference(context, COMPILE_PIT_METRICS_PREFERENCE, true);
    }

    public static float compileWeight(Context context) {
        return getFloatPreference(context, COMPILE_WEIGHT_PREFERENCE, "1.0");
    }

    private static boolean getBooleanPreference(Context context, String key, boolean default_value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        return sharedPreferences.getBoolean(key, default_value);
    }

    private static float getFloatPreference(Context context, String key, String default_value) {
        SharedPreferences sharedPreferences = getPreferences(context);
        float floatRet;
        //If float fails to parse in any way, return 'default_value'
        try {
            floatRet = Float.parseFloat(sharedPreferences.getString(key, default_value));
        } catch (Exception e) {
            floatRet = Float.parseFloat(default_value);
        }
        return floatRet;
    }
}
