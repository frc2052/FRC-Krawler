package com.team2052.frckrawler.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.google.firebase.crash.FirebaseCrash;
import com.team2052.frckrawler.BuildConfig;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.BluetoothConnection;
import com.team2052.frckrawler.bluetooth.BluetoothConstants;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.server.ServerPackage;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.ScoutUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.ObjectInputStream;
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
        Set<BluetoothDevice> devices = BluetoothUtil.getAllBluetoothDevices();
        if (devices != null) {
            if (devices.isEmpty()) {
                builder.setTitle("No devices are paired with this device");
                builder.setPositiveButton("Bluetooth Settings", (dialog, which) -> context.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS)));
            } else {
                builder.setTitle("Select Server Device");
                builder.setItems(BluetoothUtil.getDeviceNames(devices), (dialog, which) -> {
                    BluetoothDevice[] allBluetoothDevicesArray = BluetoothUtil.getAllBluetoothDevicesArray();
                    if (allBluetoothDevicesArray != null) {
                        showAskSyncDialog(context, allBluetoothDevicesArray[which], startSyncConsumer);
                    }
                });
            }
        }
        builder.create().show();
    }

    public static void startScoutSync(final Context context, Action1<BluetoothDevice> startSyncConsumer) {
        if (!BluetoothUtil.hasBluetoothAdapter()) {
            Toast.makeText(context, context.getString(R.string.bluetooth_not_supported_message), Toast.LENGTH_LONG).show();
            return;
        }
        Optional<BluetoothDevice> bluetoothDevice = ScoutUtil.getSyncDevice(context);
        if (bluetoothDevice.isPresent()) {
            showAskSyncDialog(context, bluetoothDevice.get(), startSyncConsumer);
        } else {
            showDeviceListDialog(context, startSyncConsumer);
        }
    }

    public static Observable<ScoutSyncStatus> getScoutSyncTask(Context context, BluetoothDevice device) {
        return BluetoothUtil.connectToBluetoothDevice(device)
                .map(bluetoothConnection -> {
                    AndroidSchedulers.mainThread().createWorker().schedule(() -> EventBus.getDefault().post(new ScoutSyncStartEvent()));

                    BluetoothConnection.OutputStreamWrapper outputStreamWrapper = bluetoothConnection.getOutputStreamWrapper();
                    try {
                        outputStreamWrapper
                                .writeInteger(BuildConfig.VERSION_CODE)
                                .writeInteger(BluetoothConstants.SCOUT_SYNC)
                                .writeObject(new ServerPackage(RxDBManager.getInstance(context), ScoutUtil.getScoutEvent(context)))
                                .send();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }


                    return bluetoothConnection;
                })
                .map(bluetoothConnection -> {
                    ObjectInputStream inputStream = bluetoothConnection.getInputStream();

                    ScoutPackage scoutPackage = null;

                    try {
                        int code = inputStream.readInt();
                        if (code == BluetoothConstants.OK) {
                            scoutPackage = (ScoutPackage) inputStream.readObject();
                        } else if (code == BluetoothConstants.VERSION_ERROR) {
                            return new ScoutSyncStatus(false, String.format("The server version is incompatible with your version. You are running %s and the server is running %s", BuildConfig.VERSION_NAME, inputStream.readObject()));
                        } else if (code == BluetoothConstants.EVENT_MATCH_ERROR) {
                            RxDBManager.getInstance(context).runInTx(() -> RxDBManager.getInstance(context).deleteAll());
                            return new ScoutSyncStatus(false, "This device's data did not match up with the server tablet. The data from this tablet was lost.");
                        }
                        bluetoothConnection.closeConnection();
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                        FirebaseCrash.report(e);
                        return new ScoutSyncStatus(false);
                    }

                    if (scoutPackage != null) {
                        // Knowing that the data was sent to the server, we can now delete the data on our device, and sync with the server
                        RxDBManager.getInstance(context).runInTx(() -> RxDBManager.getInstance(context).deleteAll());

                        scoutPackage.save(RxDBManager.getInstance(context), context);

                        ScoutUtil.setDeviceAsScout(context, true);
                        ScoutUtil.setSyncDevice(context, device);
                        return new ScoutSyncStatus(true);
                    }

                    return new ScoutSyncStatus(false);
                });
    }
}
