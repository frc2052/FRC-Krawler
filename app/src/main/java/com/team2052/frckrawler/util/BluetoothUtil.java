package com.team2052.frckrawler.util;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.team2052.frckrawler.bluetooth.BluetoothConnection;
import com.team2052.frckrawler.bluetooth.BluetoothConstants;
import com.team2052.frckrawler.bluetooth.events.StartBluetoothConnectionEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;
import java.util.UUID;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

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

    public static Observable<BluetoothConnection> connectToBluetoothDevice(BluetoothDevice device) {
        return Observable.just(device)
                .map(bluetoothDevice -> {
                    AndroidSchedulers.mainThread().createWorker().schedule(() -> EventBus.getDefault().post(new StartBluetoothConnectionEvent()));

                    BluetoothSocket socket = null;
                    ObjectOutputStream outputStream = null;
                    ObjectInputStream inputStream = null;

                    try {
                        socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothConstants.UUID));
                        socket.connect();

                        outputStream = new ObjectOutputStream(socket.getOutputStream());
                        inputStream = new ObjectInputStream(socket.getInputStream());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return new BluetoothConnection(socket, inputStream, outputStream);
                });
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

    public static Boolean hasBluetoothPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            int permission = ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT);
            return permission == PackageManager.PERMISSION_GRANTED;
        } else {
            // Below S this an install time permission
            return true;
        }
    }
}
