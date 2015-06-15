package com.team2052.frckrawler.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

/**
 * @author Adam
 * @since 12/9/2014.
 */
public class BluetoothUtil {

    @Nullable
    public static BluetoothDevice getDevice(String address) {
        if (!hasBluetoothAdapter())
            return null;
        return getBluetoothAdapter().getRemoteDevice(address);
    }

    @Nullable
    public static BluetoothAdapter getBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter();
    }

    public static boolean hasBluetoothAdapter() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    public static boolean isBluetoothEnabled() {
        return hasBluetoothAdapter() && getBluetoothAdapter().isEnabled();
    }


}
