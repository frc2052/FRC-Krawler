package com.team2052.frckrawler.bluetooth.scout;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.team2052.frckrawler.BuildConfig;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.BluetoothConnection;
import com.team2052.frckrawler.bluetooth.BluetoothConstants;
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.syncable.ScoutSyncable;
import com.team2052.frckrawler.bluetooth.syncable.ServerDataSyncable;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.helpers.BluetoothHelper;
import com.team2052.frckrawler.helpers.ScoutHelper;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.Set;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * @author Adam
 * @since 12/11/2014.
 */
public class ScoutSyncHandler {
    private ScoutSyncHandler() {
    }

    private static void showAskSyncDialog(Context context, BluetoothDevice device, Action1<BluetoothDevice> startSyncConsumer) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Are you sure you want to sync?");
        dialog.setMessage(String.format("Are you sure you want to sync to %s?", device.getName()));
        dialog.setPositiveButton("Yes", (dialog1, which) -> startSyncConsumer.call(device));
        dialog.setNegativeButton("No", null);
        dialog.create().show();
    }

    private static void showDeviceListDialog(Context context, Action1<BluetoothDevice> startSyncConsumer) {
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

    public static void startScoutSync(final Context context, Action1<BluetoothDevice> startSyncConsumer) {
        if (!BluetoothHelper.hasBluetoothAdapter()) {
            Toast.makeText(context, context.getString(R.string.bluetooth_not_supported_message), Toast.LENGTH_LONG).show();
            return;
        }
        Optional<BluetoothDevice> bluetoothDevice = ScoutHelper.getSyncDevice(context);
        if (bluetoothDevice.isPresent()) {
            showAskSyncDialog(context, bluetoothDevice.get(), startSyncConsumer);
        } else {
            showDeviceListDialog(context, startSyncConsumer);
        }
    }

    public static Observable<ScoutSyncStatus> getScoutSyncTask(Context context, BluetoothDevice device) {
        return BluetoothHelper.connectToBluetoothDevice(device)
                .map(bluetoothConnection -> {
                    AndroidSchedulers.mainThread().createWorker().schedule(() -> EventBus.getDefault().post(new ScoutSyncStartEvent()));

                    BluetoothConnection.OutputStreamWrapper outputStreamWrapper = bluetoothConnection.getOutputStreamWrapper();
                    try {
                        outputStreamWrapper
                                .writeInteger(BuildConfig.VERSION_CODE)
                                .writeObject(new ServerDataSyncable(context))
                                .send();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    return bluetoothConnection;
                })
                .map(bluetoothConnection -> {
                    try {
                        ScoutSyncable scoutSyncable = (ScoutSyncable) bluetoothConnection.getInputStream().readObject();
                        bluetoothConnection.closeConnection();

                        int code = scoutSyncable.getReturnCode();
                        switch (code) {
                            case BluetoothConstants.ReturnCodes.OK:
                                // Knowing that the data was sent to the server, we can now delete the data on our device, and sync with the server
                                RxDBManager.Companion.getInstance(context).runInTx(() -> RxDBManager.Companion.getInstance(context).deleteAll());

                                scoutSyncable.saveToScout(context);

                                ScoutHelper.setDeviceAsScout(context, true);
                                ScoutHelper.setSyncDevice(context, device);
                                return new ScoutSyncStatus(true);
                            case BluetoothConstants.ReturnCodes.VERSION_ERROR:
                                return new ScoutSyncStatus(false, "The server version is incompatible with your version");
                            case BluetoothConstants.ReturnCodes.EVENT_MATCH_ERROR:
                                RxDBManager.Companion.getInstance(context).runInTx(() -> RxDBManager.Companion.getInstance(context).deleteAll());
                                return new ScoutSyncStatus(false, "This device's data did not match up with the server tablet. The data from this tablet was lost.");
                            default:
                                return new ScoutSyncStatus(false, "Scout got response code that this device couldn't handle. Try updating");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new ScoutSyncStatus(false);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                        return new ScoutSyncStatus(false);
                    }
                });
    }
}