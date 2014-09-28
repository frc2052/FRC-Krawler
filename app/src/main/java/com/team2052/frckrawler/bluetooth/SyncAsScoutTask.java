package com.team2052.frckrawler.bluetooth;

import android.bluetooth.*;
import android.content.*;
import android.os.AsyncTask;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.database.*;
import com.team2052.frckrawler.database.models.*;

import java.io.*;
import java.util.*;

public class SyncAsScoutTask extends AsyncTask<BluetoothDevice, Void, Integer>
{
    private static int SYNC_SUCCESS = 1;
    private static int SYNC_SERVER_OPEN = 2;
    private static int SYNC_CANCELLED = 3;
    private static int SYNC_ERROR = 4;
    private static int tasksRunning = 0;

    private volatile String deviceName;
    private Context context;
    private SyncCallbackHandler handler;

    public SyncAsScoutTask(Context c, SyncCallbackHandler h)
    {
        deviceName = "device";
        context = c.getApplicationContext();
        handler = h;
    }

    public static boolean isTaskRunning()
    {
        return tasksRunning > 0;
    }

    @Override
    protected void onPreExecute()
    {
        tasksRunning++;
        handler.onSyncStart("device");
    }

    @Override
    protected Integer doInBackground(BluetoothDevice... dev)
    {
        deviceName = dev[0].getName();
        if (Server.getInstance(context).isOpen())
            return SYNC_SERVER_OPEN;
        try {
            Log.i("FRCKrawler", "Syncing With Server");
            long startTime = System.currentTimeMillis();
            BluetoothSocket serverSocket = dev[0].createRfcommSocketToServiceRecord(UUID.fromString(BluetoothInfo.UUID));
            serverSocket.connect();

            if (isCancelled())
                return SYNC_CANCELLED;

            //Open the streams
            InputStream inStream = serverSocket.getInputStream();
            ObjectInputStream ioStream = new ObjectInputStream(inStream);
            OutputStream outStream = serverSocket.getOutputStream();
            ObjectOutputStream ooStream = new ObjectOutputStream(outStream);

            //Get the data to send
            List<MetricMatchData> metricMatchData = DBManager.loadAllFromType(MetricMatchData.class);

            if (isCancelled())
                return SYNC_CANCELLED;

            //Write the scout data
            ooStream.writeInt(BluetoothInfo.SCOUT);
            ooStream.writeObject(metricMatchData);
            ooStream.flush();

            //Clear out the old data after it is sent
            //Cleaning out the parent tables clears the whole database
            new Delete().from(Game.class).execute();
            new Delete().from(Team.class).execute();
            new Delete().from(User.class).execute();

            if (isCancelled())
                return SYNC_CANCELLED;

            //Start fdthe reading thread
            //Set the current event id hosted by the server
            Event event1 = (Event) ioStream.readObject();
            event1.game.save();
            event1.save();

            SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, event1.getId());
            editor.apply();

            List<Metric> inMetric = (List<Metric>) ioStream.readObject();
            List<User> inUsers = (List<User>) ioStream.readObject();
            List<RobotEvents> inRobots = (List<RobotEvents>) ioStream.readObject();
            Schedule inSchedule = (Schedule) ioStream.readObject();

            if (isCancelled())
                return SYNC_CANCELLED;

            ActiveAndroid.beginTransaction();

            for (RobotEvents robot : inRobots) {
                robot.saveAll();
            }

            for (Match match : inSchedule.matches) {
                match.saveAll();
            }

            for (Metric metric1 : inMetric) {
                metric1.save();
            }

            for (User user : inUsers) {
                user.save();
            }

            ActiveAndroid.setTransactionSuccessful();
            ActiveAndroid.endTransaction();

            //Close the streams
            ooStream.close();
            outStream.close();
            serverSocket.close();
            Log.d("FRCKrawler", "Time: " + (System.currentTimeMillis() - startTime));
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FRCKrawler", "Scout not synced, I/O error.");
            return SYNC_ERROR;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.e("FRCKrawler", "Scout not synced, class not found.");
            return SYNC_ERROR;
        }
        return SYNC_SUCCESS;
    }

    @Override
    protected void onPostExecute(Integer i)
    {
        tasksRunning--;
        if (i == SYNC_SUCCESS)
            handler.onSyncSuccess(deviceName);
        else if (i == SYNC_ERROR)
            handler.onSyncError(deviceName);
        else if (i == SYNC_CANCELLED)
            handler.onSyncCancel(deviceName);
    }
}
