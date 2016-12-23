package com.team2052.frckrawler.util;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.team2052.frckrawler.Constants;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;

public class ScoutUtil {
    public static void setDeviceAsScout(Context context, boolean isScout) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0).edit();
        editor.putBoolean(Constants.IS_SCOUT_PREF, isScout);
        editor.apply();
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

    public static BluetoothDevice getSyncDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        String address = prefs.getString(Constants.MAC_ADDRESS_PREF, "null");
        if (address.equals("null")) {
            return null;
        }
        return BluetoothUtil.getDevice(address);
    }

    @Nullable
    public static Event getScoutEvent(Context context) {
        SharedPreferences scoutPrefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        if (scoutPrefs.getLong(Constants.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
            return RxDBManager.getInstance(context).getEventsTable().load(scoutPrefs.getLong(Constants.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
        }
        return null;
    }

    public static void resetSyncDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.MAC_ADDRESS_PREF, "null");
        prefsEditor.apply();
    }
}
