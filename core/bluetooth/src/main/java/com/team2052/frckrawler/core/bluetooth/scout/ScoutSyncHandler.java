package com.team2052.frckrawler.core.bluetooth.scout;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.team2052.frckrawler.core.bluetooth.BluetoothHelper;
import com.team2052.frckrawler.core.bluetooth.R;
import com.team2052.frckrawler.core.common.ScoutHelper;

import java.util.Set;

import rx.functions.Action1;


/**
 * @author Adam
 * @since 12/11/2014.
 */
public class ScoutSyncHandler {
    private ScoutSyncHandler() {
    }

    private static void showAskSyncDialog(Context context, BluetoothDevice device, Action1<String> startSyncConsumer) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Are you sure you want to sync?");
        dialog.setMessage(String.format("Are you sure you want to sync to %s?", device.getName()));
        dialog.setPositiveButton("Yes", (dialog1, which) -> startSyncConsumer.call(device.getAddress()));
        dialog.setNegativeButton("No", null);
        dialog.create().show();
    }

    private static void showDeviceListDialog(Context context, Action1<String> startSyncConsumer) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        Set<BluetoothDevice> devices = BluetoothHelper.getAllBluetoothDevices();
        if (devices != null) {
            if (devices.isEmpty()) {
                builder.setTitle("No devices are paired with this device");
                builder.setPositiveButton("Bluetooth Settings", (dialog, which) -> context.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS)));
            } else {
                builder.setTitle("Select Server Device");
                builder.setItems(BluetoothHelper.getDeviceNames(devices), (dialog, which) -> {
                    BluetoothDevice[] allBluetoothDevicesArray = BluetoothHelper.getAllBluetoothDevicesArray();
                    if (allBluetoothDevicesArray != null) {
                        showAskSyncDialog(context, allBluetoothDevicesArray[which], startSyncConsumer);
                    }
                });
            }
        }
        builder.create().show();
    }

    public static void startScoutSync(final Context context, Action1<String> macAddressConsumer) {
        if (!BluetoothHelper.hasBluetoothAdapter()) {
            //Toast.makeText(context, context.getString(R.string.bluetooth_not_supported_message), Toast.LENGTH_LONG).show();
            return;
        }
        Optional<BluetoothDevice> bluetoothDevice = ScoutHelper.getSyncDevice(context);
        if (bluetoothDevice.isPresent()) {
            showAskSyncDialog(context, bluetoothDevice.get(), macAddressConsumer);
        } else {
            showDeviceListDialog(context, macAddressConsumer);
        }
    }
}