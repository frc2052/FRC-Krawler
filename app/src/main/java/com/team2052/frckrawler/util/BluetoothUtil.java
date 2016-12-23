package com.team2052.frckrawler.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.support.annotation.Nullable;

import java.util.Set;

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

    @Nullable
    public static Set<BluetoothDevice> getAllBluetoothDevices() {
        if (!hasBluetoothAdapter()) {
            return null;
        }
        return getBluetoothAdapter().getBondedDevices();
    }

    @Nullable
    public static BluetoothDevice[] getAllBluetoothDevicesArray() {
        if (!hasBluetoothAdapter()) {
            return null;
        }
        return BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray(new BluetoothDevice[BluetoothAdapter.getDefaultAdapter().getBondedDevices().size()]);
    }
}
