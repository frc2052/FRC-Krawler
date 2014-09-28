package com.team2052.frckrawler.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;

import com.team2052.frckrawler.database.DBManager;

public class SyncAsSummaryTask extends AsyncTask<BluetoothDevice, Void, Integer>
{
    private static int SYNC_SUCCESS = 1;
    private static int SYNC_SERVER_OPEN = 2;
    private static int SYNC_CANCELLED = 3;
    private static int SYNC_ERROR = 4;
    private static int tasksRunning = 0;
    private volatile String deviceName;
    private Context context;
    private DBManager dbManager;
    private SyncCallbackHandler handler;

    public SyncAsSummaryTask(Context _context, SyncCallbackHandler _handler)
    {
        deviceName = "device";
        context = _context;
        handler = _handler;
    }

    public static boolean isTaskRunning()
    {
        return tasksRunning > 0;
    }

    @Override
    protected void onPreExecute()
    {
        tasksRunning++;
    }

    @Override
    protected Integer doInBackground(BluetoothDevice... dev)
    {
        deviceName = dev[0].getName();
        if (Server.getInstance(context).isOpen())
            return SYNC_SERVER_OPEN;
        //Get data from server
        //Put data in the database
        return SYNC_SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer result)
    {
        tasksRunning--;
        if (result == SYNC_SUCCESS)
            handler.onSyncSuccess(deviceName);
        else if (result == SYNC_ERROR)
            handler.onSyncError(deviceName);
        else if (result == SYNC_CANCELLED)
            handler.onSyncCancel(deviceName);
    }
}
