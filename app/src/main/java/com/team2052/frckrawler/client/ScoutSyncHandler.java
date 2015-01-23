package com.team2052.frckrawler.client;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.core.util.Utilities;

import de.greenrobot.event.EventBus;

/**
 * @author Adam
 * @since 12/11/2014.
 */
@SuppressWarnings("unused")
public class ScoutSyncHandler {
    static volatile ScoutSyncHandler instance;
    private final Context context;

    private SyncScoutTask syncAsScoutTask = null;
    private BluetoothDevice mCurrentSyncingDevice;

    public ScoutSyncHandler(Context context) {
        this.context = context;
        EventBus.getDefault().register(this);
    }

    /**
     * To be used across the whole application*
     */
    public static ScoutSyncHandler getInstance(Context context) {
        if (instance == null) synchronized (ScoutSyncHandler.class) {
            if (instance == null)
                instance = new ScoutSyncHandler(context);
        }
        return instance;
    }

    public void startSync(BluetoothDevice device) {
        cancelAllRunningSyncs();
        mCurrentSyncingDevice = device;
        syncAsScoutTask = new SyncScoutTask(context);
        syncAsScoutTask.execute(device);
    }

    public void startSync(String address) {
        startSync(Utilities.BluetoothUtil.getDevice(address));
    }

    public void cancelAllRunningSyncs() {
        if (syncAsScoutTask != null)
            syncAsScoutTask.cancel(true);
    }

    public boolean isSyncRunning() {
        return syncAsScoutTask != null;
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event) {
        //Reset task because it isn't running
        syncAsScoutTask = null;
        Utilities.ScoutUtil.setDeviceAsScout(context, true);
        Utilities.ScoutUtil.setSyncDevice(context, mCurrentSyncingDevice);
        Toast.makeText(context, context.getString(R.string.sync_successful_message), Toast.LENGTH_LONG).show();
        LogHelper.info("SyncHandler: Sync Successful!");
        mCurrentSyncingDevice = null;
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncErrorEvent errorEvent) {
        //Alert that there was an mErrorView
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.sync_error_title));
        builder.setMessage(context.getString(R.string.sync_error_message));
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
        mCurrentSyncingDevice = null;
    }

    public void startScoutSync() {
        if (!Utilities.BluetoothUtil.hasBluetoothAdapter()) {
            Toast.makeText(context, context.getString(R.string.bluetooth_not_supported_message), Toast.LENGTH_LONG).show();
            return;
        }

        BluetoothDevice bluetoothDevice = Utilities.ScoutUtil.getSyncDevice(context);
        if (bluetoothDevice != null) {
            startSync(bluetoothDevice);
        } else {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Server Device");
            builder.setItems(Utilities.ScoutUtil.getDeviceNames(Utilities.ScoutUtil.getAllBluetoothDevices()), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startSync(Utilities.ScoutUtil.getAllBluetoothDevicesArray()[which]);
                }
            });
            builder.create().show();
        }
    }

}