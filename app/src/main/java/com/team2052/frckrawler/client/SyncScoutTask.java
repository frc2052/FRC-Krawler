package com.team2052.frckrawler.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;

import com.team2052.frckrawler.client.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.core.BluetoothInfo;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.server.ServerPackage;

import org.acra.ACRA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * @author Adam, Charlie
 */
public class SyncScoutTask extends AsyncTask<BluetoothDevice, Void, Integer> {
    private static final int SYNC_SUCCESS = 1;
    private static final int SYNC_CANCELLED = 3;
    private static final int SYNC_ERROR = 4;
    private static int tasksRunning = 0;
    private final DBManager mDbManager;


    private volatile String deviceName;
    private Context context;

    public SyncScoutTask(Context c) {
        deviceName = "device";
        context = c.getApplicationContext();
        mDbManager = ((FRCKrawler) c.getApplicationContext()).getDBSession();
    }

    public static boolean isTaskRunning() {
        return tasksRunning > 0;
    }

    @Override
    protected Integer doInBackground(BluetoothDevice... dev) {
        deviceName = dev[0].getName();
        LogHelper.info("Syncing With Server");
        BluetoothDevice bluetoothDevice = dev[0];

        if (bluetoothDevice != null) {
            BluetoothSocket serverSocket = null;

            try {
                serverSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothInfo.UUID));
                serverSocket.connect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (serverSocket != null) {
                ObjectInputStream ioStream = null;
                ObjectOutputStream ooStream = null;
                try {
                    ioStream = new ObjectInputStream(serverSocket.getInputStream());
                    ooStream = new ObjectOutputStream(serverSocket.getOutputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (ioStream != null && ooStream != null) {
                    try {
                        ooStream.writeInt(BluetoothInfo.ConnectionType.SCOUT_SYNC.ordinal());
                        ooStream.writeObject(new ServerPackage(mDbManager));
                        ooStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    deleteAllData();

                    try {
                        ((ScoutPackage) ioStream.readObject()).save(mDbManager, context);
                        ooStream.close();

                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                        ACRA.getErrorReporter().handleException(e);
                    }
                }

                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        LogHelper.info("Done Syncing. Ended with code " + i);
        tasksRunning--;
        if (i == SYNC_SUCCESS)
            EventBus.getDefault().post(new ScoutSyncSuccessEvent());
        else if (i == SYNC_ERROR)
            EventBus.getDefault().post(new ScoutSyncErrorEvent());
        else if (i == SYNC_CANCELLED)
            EventBus.getDefault().post(new ScoutSyncCancelledEvent());
    }

    public void deleteAllData() {
        mDbManager.getDaoSession().runInTx(() -> {
            //Delete everything
            mDbManager.getDaoSession().getGameDao().deleteAll();
            mDbManager.getDaoSession().getMatchDao().deleteAll();
            mDbManager.getDaoSession().getRobotDao().deleteAll();
            mDbManager.getDaoSession().getRobotEventDao().deleteAll();
            mDbManager.getDaoSession().getMatchDao().deleteAll();
            mDbManager.getDaoSession().getTeamDao().deleteAll();
            mDbManager.getDaoSession().getUserDao().deleteAll();
            mDbManager.getDaoSession().getMetricDao().deleteAll();
            mDbManager.getDaoSession().getPitDataDao().deleteAll();
            mDbManager.getDaoSession().getMatchDataDao().deleteAll();
            mDbManager.getDaoSession().getMatchCommentDao().deleteAll();
            mDbManager.getDaoSession().getRobotPhotoDao().deleteAll();
        });
    }
}
