package com.team2052.frckrawler.util;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import com.google.common.base.Optional;
import com.team2052.frckrawler.Constants;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.theme.ThemeChangedEvent;
import com.team2052.frckrawler.theme.Themes;

import org.greenrobot.eventbus.EventBus;

import rx.functions.Action1;

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

    public static Optional<BluetoothDevice> getSyncDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        String address = prefs.getString(Constants.MAC_ADDRESS_PREF, "null");
        if (address.equals("null")) {
            return Optional.absent();
        }
        return Optional.of(BluetoothUtil.getDevice(address));
    }

    @Nullable
    public static Event getScoutEvent(Context context) {
        SharedPreferences scoutPrefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        if (scoutPrefs.getLong(Constants.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
            return RxDBManager.getInstance(context).getEventsTable().load(scoutPrefs.getLong(Constants.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
        }
        return null;
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

    public static void showAskThemeDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.select_theme);
        builder.setSingleChoiceItems(Themes.getNames(), ScoutUtil.getScoutTheme(context), (dialog1, which) -> {
        });
        builder.setPositiveButton("Ok", (dialog, which) -> {
            int themeIndex = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
            ScoutUtil.setScoutTheme(context, themeIndex);
            EventBus.getDefault().post(new ThemeChangedEvent());
        });
        builder.create().show();
    }

    public static void resetSyncDevice(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString(Constants.MAC_ADDRESS_PREF, "null");
        prefsEditor.apply();
    }
}
