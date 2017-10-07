package com.team2052.frckrawler.util;

import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

public class Logger {
    public static void d(String tag, String msg) {
        FirebaseCrash.logcat(Log.DEBUG, tag, msg);
    }

    public static void i(String tag, String msg) {
        FirebaseCrash.logcat(Log.INFO, tag, msg);
    }

    public static void e(String tag, String msg) {
        FirebaseCrash.logcat(Log.ERROR, tag, msg);
    }
}
