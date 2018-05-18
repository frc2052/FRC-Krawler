package com.team2052.frckrawler.core.common;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.google.common.base.Optional;

import org.greenrobot.eventbus.EventBus;

public class ScoutHelper {
    public static void setDeviceAsScout(Context context, boolean isScout) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0).edit();
        editor.putBoolean(Constants.IS_SCOUT_PREF, isScout);
        editor.apply();
    }

    public static boolean isDeviceScout(Context context) {
        SharedPreferences scoutPrefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        return scoutPrefs.getBoolean(Constants.IS_SCOUT_PREF, false);
    }

    public static boolean getDeviceIsScout(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        return preferences.getBoolean(Constants.IS_SCOUT_PREF, false);
    }

    public static void setSyncDevice(Context context, BluetoothDevice device) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.MAC_ADDRESS_PREF, device.getAddress());
        prefsEditor.apply();
    }

    public static Optional<BluetoothDevice> getSyncDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        String address = prefs.getString(Constants.MAC_ADDRESS_PREF, "null");
        if (address.equals("null")) {
            return Optional.absent();
        }
        return Optional.absent();
    }

    public static int getScoutTheme(Context context) {
        SharedPreferences scoutPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return scoutPrefs.getInt(Constants.SCOUT_THEME_SELECTION, 0);
    }

    public static void setScoutTheme(Context context, int theme) {
        SharedPreferences.Editor scoutPrefs = PreferenceManager.getDefaultSharedPreferences(context).edit();
        scoutPrefs.putInt(Constants.SCOUT_THEME_SELECTION, theme);
        scoutPrefs.apply();
    }

    public static void resetSyncDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.MAC_ADDRESS_PREF, "null");
        prefsEditor.apply();
    }
}
