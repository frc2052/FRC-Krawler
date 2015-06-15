package com.team2052.frckrawler.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;

import java.util.Set;

/**
 * Created by Adam on 5/3/2015.
 */
public class ScoutUtil {
    @Nullable
    public static Set<BluetoothDevice> getAllBluetoothDevices() {
        if (!BluetoothUtil.hasBluetoothAdapter()) {
            return null;
        }
        return BluetoothUtil.getBluetoothAdapter().getBondedDevices();
    }


    @Nullable
    public static BluetoothDevice[] getAllBluetoothDevicesArray() {
        if (!BluetoothUtil.hasBluetoothAdapter()) {
            return null;
        }
        return BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray(new BluetoothDevice[BluetoothAdapter.getDefaultAdapter().getBondedDevices().size()]);
    }

    public static CharSequence[] getDeviceNames(@Nullable Set<BluetoothDevice> bluetoothDevices) {
        if (bluetoothDevices == null) {
            return new CharSequence[0];
        }

        BluetoothDevice[] devices = bluetoothDevices.toArray(new BluetoothDevice[bluetoothDevices.size()]);
        CharSequence[] deviceNames = new CharSequence[devices.length];

        for (int i = 0; i < deviceNames.length; i++) {
            deviceNames[i] = devices[i].getName();
        }

        return deviceNames;
    }

    public static void setDeviceAsScout(Context context, boolean isScout) {
        SharedPreferences.Editor editor = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0).edit();
        editor.putBoolean(GlobalValues.IS_SCOUT_PREF, isScout);
        editor.apply();
    }

    public static boolean getDeviceIsScout(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        return preferences.getBoolean(GlobalValues.IS_SCOUT_PREF, false);
    }

    public static void setSyncDevice(Context context, BluetoothDevice device) {
        SharedPreferences prefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, device.getAddress());
        prefsEditor.apply();
    }

    public static BluetoothDevice getSyncDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        String address = prefs.getString(GlobalValues.MAC_ADRESS_PREF, "null");
        if (address.equals("null")) {
            return null;
        }
        return BluetoothUtil.getDevice(address);
    }

    @Nullable
    public static Event getScoutEvent(Context context, DBManager mDBManager) {
        SharedPreferences scoutPrefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        if (scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
            return mDBManager.mEvents.load(scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
        }
        return null;
    }

    public static void resetSyncDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, "null");
        prefsEditor.apply();
    }
}
