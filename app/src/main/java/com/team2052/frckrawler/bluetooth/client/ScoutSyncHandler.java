package com.team2052.frckrawler.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.ScoutUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Set;


/**
 * @author Adam
 * @since 12/11/2014.
 */
public class ScoutSyncHandler extends ContextWrapper {
    static ScoutSyncHandler instance;

    private SyncScoutTask syncAsScoutTask = null;
    private BluetoothDevice mCurrentSyncingDevice;

    public ScoutSyncHandler(Context context) {
        super(context);
        EventBus.getDefault().register(this);
    }

    public static synchronized ScoutSyncHandler getInstance(Context context) {
        if (instance == null) {
            instance = new ScoutSyncHandler(context);
        }
        return instance;
    }

    public void startSync(String address, Context context) {
        startSync(BluetoothUtil.getDevice(address), context);
    }

    public void startSync(BluetoothDevice device, Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Are you sure you want to sync?");
        dialog.setMessage("Are you sure you want to sync to " + device.getName() + "?");
        dialog.setPositiveButton("Yes", (dialog1, which) -> {
            cancelAllRunningSyncs();
            mCurrentSyncingDevice = device;
            syncAsScoutTask = new SyncScoutTask(context);
            syncAsScoutTask.execute(device);
        });
        dialog.setNegativeButton("No", null);
        dialog.create().show();
    }

    public void cancelAllRunningSyncs() {
        if (syncAsScoutTask != null)
            syncAsScoutTask.cancel(true);
    }

    public boolean isSyncRunning() {
        return syncAsScoutTask != null;
    }

    @Subscribe
    public void onEvent(ScoutSyncSuccessEvent event) {
        //Reset task because it isn't running
        syncAsScoutTask = null;
        ScoutUtil.setDeviceAsScout(getBaseContext(), true);
        ScoutUtil.setSyncDevice(getBaseContext(), mCurrentSyncingDevice);
        mCurrentSyncingDevice = null;
    }

    public void startScoutSync(final Context context) {
        if (!BluetoothUtil.hasBluetoothAdapter()) {
            Toast.makeText(context, context.getString(R.string.bluetooth_not_supported_message), Toast.LENGTH_LONG).show();
            return;
        }


        BluetoothDevice bluetoothDevice = ScoutUtil.getSyncDevice(context);
        if (bluetoothDevice != null) {
            startSync(bluetoothDevice, context);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            Set<BluetoothDevice> devices = ScoutUtil.getAllBluetoothDevices();
            if (devices != null) {
                if (devices.isEmpty()) {
                    builder.setTitle("No Devices are paired with this device");
                    builder.setPositiveButton("Bluetooth Settings", (dialog, which) -> {
                        context.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    });
                } else {
                    builder.setTitle("Select Server Device");
                    builder.setItems(ScoutUtil.getDeviceNames(devices), (dialog, which) -> {
                        BluetoothDevice[] allBluetoothDevicesArray = ScoutUtil.getAllBluetoothDevicesArray();
                        if (allBluetoothDevicesArray != null) {
                            ScoutSyncHandler.this.startSync(allBluetoothDevicesArray[which], context);
                        }
                    });
                }
            }
            builder.create().show();
        }
    }

}
