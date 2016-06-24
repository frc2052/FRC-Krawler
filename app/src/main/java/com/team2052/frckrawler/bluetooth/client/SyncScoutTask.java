package com.team2052.frckrawler.bluetooth.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.team2052.frckrawler.BuildConfig;
import com.team2052.frckrawler.bluetooth.BluetoothConstants;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.bluetooth.server.ServerPackage;
import com.team2052.frckrawler.database.DBManager;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

/**
 * @author Adam, Charlie
 */
public class SyncScoutTask extends AsyncTask<BluetoothDevice, Void, Integer> {
    private static final int SYNC_SUCCESS = 1;
    private static final int SYNC_CANCELLED = 3;
    private static final int SYNC_ERROR = 4;
    private static int tasksRunning = 0;
    private final DBManager mDbManager;
    private final String TAG = SyncScoutTask.class.getSimpleName();

    private String errorMessage = null;


    private volatile String deviceName;
    private Context context;

    public SyncScoutTask(Context c) {
        deviceName = "device";
        context = c.getApplicationContext();
        mDbManager = DBManager.getInstance(context);
    }

    public static boolean isTaskRunning() {
        return tasksRunning > 0;
    }

    @Override
    protected Integer doInBackground(BluetoothDevice... dev) {
        deviceName = dev[0].getName();
        Log.i(TAG, "Syncing With Server");
        BluetoothDevice bluetoothDevice = dev[0];

        if (bluetoothDevice != null) {
            BluetoothSocket serverSocket = null;

            try {
                serverSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothConstants.UUID));
                serverSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                //Server is most likely off
                return SYNC_ERROR;
            }

            ObjectInputStream ioStream = null;
            ObjectOutputStream ooStream = null;
            try {
                ioStream = new ObjectInputStream(serverSocket.getInputStream());
                ooStream = new ObjectOutputStream(serverSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
                return SYNC_ERROR;
            }

            try {
                ooStream.writeInt(BuildConfig.VERSION_CODE);
                ooStream.writeInt(BluetoothConstants.SCOUT_SYNC);
                ooStream.writeObject(new ServerPackage(mDbManager));
                ooStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return SYNC_ERROR;
            }

            deleteAllData();

            ScoutPackage scoutPackage = null;
            try {
                int code = ioStream.readInt();
                if (code == BluetoothConstants.OK) {
                    scoutPackage = (ScoutPackage) ioStream.readObject();
                } else if (code == BluetoothConstants.VERSION_ERROR) {
                    errorMessage = String.format("The server version is incompatible with your version. You are running %s and the server is running %s", BuildConfig.VERSION_NAME, ioStream.readObject());
                    return SYNC_ERROR;
                }
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
                return SYNC_ERROR;
            }

            if (scoutPackage != null) {
                scoutPackage.save(mDbManager, context);
            }

            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return SYNC_SUCCESS;
    }

    @Override
    protected void onPreExecute() {
        tasksRunning++;
        EventBus.getDefault().post(new ScoutSyncStartEvent());
    }

    @Override
    protected void onPostExecute(Integer i) {
        Log.i(TAG, "Done Syncing. Ended with code " + i);
        tasksRunning--;
        if (i == SYNC_SUCCESS)
            EventBus.getDefault().post(new ScoutSyncSuccessEvent());
        else if (i == SYNC_ERROR)
            EventBus.getDefault().post(new ScoutSyncErrorEvent(errorMessage));
        else if (i == SYNC_CANCELLED)
            EventBus.getDefault().post(new ScoutSyncCancelledEvent());
    }

    public void deleteAllData() {
        mDbManager.runInTx(mDbManager::deleteAll);
    }
}
