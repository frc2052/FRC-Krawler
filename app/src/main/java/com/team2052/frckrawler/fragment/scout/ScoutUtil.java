package com.team2052.frckrawler.fragment.scout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.util.BluetoothUtil;

import java.util.Set;

/**
 * @author Adam
 * @since 12/5/2014.
 */
public class ScoutUtil
{
    @Nullable
    public static Set<BluetoothDevice> getAllBluetoothDevices()
    {
        if (BluetoothUtil.hasBluetoothAdapter()) {
            return null;
        }
        return BluetoothUtil.getBluetoothAdapter().getBondedDevices();
    }


    @Nullable
    public static BluetoothDevice[] getAllBluetoothDevicesArray()
    {
        if (BluetoothUtil.hasBluetoothAdapter()) {
            return null;
        }
        return BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray(new BluetoothDevice[BluetoothAdapter.getDefaultAdapter().getBondedDevices().size()]);
    }

    public static CharSequence[] getDeviceNames(@Nullable Set<BluetoothDevice> bluetoothDevices)
    {
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

    public static void setDeviceAsScout(Context context, boolean isScout)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0).edit();
        editor.putBoolean(GlobalValues.IS_SCOUT_PREF, isScout);
        editor.apply();
    }

    public static boolean getDeviceIsScout(Context context)
    {
        SharedPreferences preferences = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        return preferences.getBoolean(GlobalValues.IS_SCOUT_PREF, false);
    }
}
