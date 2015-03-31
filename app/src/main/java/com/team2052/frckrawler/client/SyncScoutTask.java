package com.team2052.frckrawler.client;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.common.base.Strings;
import com.team2052.frckrawler.client.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.core.BluetoothInfo;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.util.LogHelper;
import com.team2052.frckrawler.server.Server;
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
    private static final int SYNC_SERVER_OPEN = 2;
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
        if (Server.getInstance(context).isOpen())
            return SYNC_SERVER_OPEN;
        try {
            LogHelper.info("Syncing With Server");
            LogHelper.info("Syncing with: " + deviceName);
            BluetoothSocket serverSocket = dev[0].createRfcommSocketToServiceRecord(UUID.fromString(BluetoothInfo.UUID));
            serverSocket.connect();
            if (isCancelled())
                return SYNC_CANCELLED;
            //Open the streams
            ObjectInputStream ioStream = new ObjectInputStream(serverSocket.getInputStream());
            ObjectOutputStream ooStream = new ObjectOutputStream(serverSocket.getOutputStream());
            if (isCancelled())
                return SYNC_CANCELLED;
            //Write the scout data
            ooStream.writeInt(BluetoothInfo.ConnectionType.SCOUT_SYNC.ordinal());
            ooStream.writeObject(new ServerPackage(mDbManager));
            ooStream.flush();
            deleteAllData();
            if (isCancelled())
                return SYNC_CANCELLED;
            ((ScoutPackage) ioStream.readObject()).save(mDbManager, context);
            ooStream.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("FRCKrawler",  e.getMessage());
            if(!Strings.isNullOrEmpty(e.getMessage())){
                if(e.getMessage().contains("read failed, socket might closed or timeout, read ret: -1")){
                    return SYNC_ERROR;
                }
            }
            ACRA.getErrorReporter().handleException(e);
            return SYNC_ERROR;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            ACRA.getErrorReporter().handleException(e);
            return SYNC_ERROR;
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
