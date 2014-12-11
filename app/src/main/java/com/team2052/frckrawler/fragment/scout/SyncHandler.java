package com.team2052.frckrawler.fragment.scout;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.team2052.frckrawler.bluetooth.SyncAsScoutTask;
import com.team2052.frckrawler.events.scout.ScoutSyncErrorEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.LogHelper;

import de.greenrobot.event.EventBus;

/**
 * @author Adam
 * @since 12/11/2014.
 */
@SuppressWarnings("unused")
public class SyncHandler
{
    static volatile SyncHandler instance;
    private final Context context;

    private SyncAsScoutTask syncAsScoutTask = null;

    public SyncHandler(Context context)
    {
        this.context = context;
        EventBus.getDefault().register(this);
    }

    /**
     * To be used across the whole application*
     */
    @SuppressWarnings("unused")
    public static SyncHandler getInstance(Context context)
    {
        if (instance == null) {
            synchronized (SyncHandler.class) {
                if (instance == null) {
                    instance = new SyncHandler(context);
                }
            }
        }
        return instance;
    }

    public void startSync(BluetoothDevice device)
    {
        cancelAllRunningSyncs();
        ScoutUtil.setSyncDevice(context, device);
        syncAsScoutTask = new SyncAsScoutTask(context);
        syncAsScoutTask.execute(device);
    }

    public void startSync(String address)
    {
        startSync(BluetoothUtil.getDevice(address));
    }

    public void cancelAllRunningSyncs()
    {
        if (syncAsScoutTask != null)
            syncAsScoutTask.cancel(true);
    }

    public boolean isSyncRunning()
    {
        return syncAsScoutTask != null;
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event)
    {
        //Reset task because it isn't running
        syncAsScoutTask = null;
        ScoutUtil.setDeviceAsScout(context, true);
        Toast.makeText(context, "Sync Successful", Toast.LENGTH_LONG).show();
        LogHelper.info("SyncHandler: Sync Successful!");
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncErrorEvent errorEvent)
    {
        //Alert that there was an error
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Sync Error");
        builder.setMessage("There was an error in syncing with the server. Make sure " + "that the server device is turned on and is running the FRCKrawler server.");
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public void startScoutSync()
    {
        if (!BluetoothUtil.hasBluetoothAdapter())
            return;

        if (!BluetoothUtil.isBluetoothEnabled()) {
            //Yell at them to enable it.
        }

        BluetoothDevice bluetoothDevice = ScoutUtil.getSyncDevice(context);
        if (bluetoothDevice != null) {
            startSync(bluetoothDevice);
        } else if (BluetoothUtil.hasBluetoothAdapter()) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Server Device");
            builder.setItems(ScoutUtil.getDeviceNames(ScoutUtil.getAllBluetoothDevices()), new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {
                    if (!BluetoothUtil.hasBluetoothAdapter())
                        Toast.makeText(context, "Sorry, your device does not support " + "Bluetooth. You may not sync with another database.", Toast.LENGTH_LONG).show();
                    else
                        startSync(ScoutUtil.getAllBluetoothDevicesArray()[which]);
                }
            });
            builder.show();
        } else {
            Toast.makeText(context, "Sorry, your device does not support Bluetooth. " + "You are unable to sync with a server.", Toast.LENGTH_LONG).show();
        }
    }


}
