package com.team2052.frckrawler.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.Schedule;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Match;
import com.team2052.frckrawler.database.models.MatchData;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class SyncAsScoutTask extends AsyncTask<BluetoothDevice, Void, Integer> {
    //TODO
    private static int SYNC_SUCCESS = 1;
    private static int SYNC_SERVER_OPEN = 2;
    private static int SYNC_CANCELLED = 3;
    private static int SYNC_ERROR = 4;
    private static int tasksRunning = 0;
    private volatile String deviceName;
    private Context context;
    private SyncCallbackHandler handler;

    public SyncAsScoutTask(Context c, SyncCallbackHandler h) {
        deviceName = "device";
        context = c.getApplicationContext();
        handler = h;
    }

    public static boolean isTaskRunning() {
        return tasksRunning > 0;
    }

    @Override
    protected void onPreExecute() {
        tasksRunning++;
        handler.onSyncStart("device");
    }

    @Override
    protected Integer doInBackground(BluetoothDevice... dev) {
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
            Event event = DBManager.loadFromEvents().executeSingle();
            List<MatchData> matchDataArr = DBManager.loadAllFromType(MatchData.class);
            if (isCancelled())
                return SYNC_CANCELLED;
            //Write the scout data
            ooStream.writeInt(BluetoothInfo.SCOUT);
            ooStream.writeObject(event);
            ooStream.writeObject(matchDataArr);
            ooStream.flush();
            //Clear out the old data after it is sent
            DBManager.deleteAllFromType(MatchData.class);
            DBManager.deleteAllFromType(Event.class);
            if (isCancelled())
                return SYNC_CANCELLED;
            //Start the reading thread
            List<Metric> inMetric = (List<Metric>) ioStream.readObject();
            List<User> inUsers = (List<User>) ioStream.readObject();
            List<Robot> inRobots = (List<Robot>) ioStream.readObject();
            Schedule inSchedule = (Schedule) ioStream.readObject();

            if (isCancelled())
                return SYNC_CANCELLED;

            for(Robot robot: inRobots){
                robot.team.save();
                robot.game.save();
                robot.save();
            }

            for (Match match : inSchedule.matches) {
                match.alliance.save();
                match.event.save();
                match.save();
            }

            for (Metric metric1 : inMetric) {
                Log.d("FRCKrawler", metric1.name);
                metric1.save();
            }

            for (User user : inUsers) {
                user.save();
            }

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
    protected void onPostExecute(Integer i) {
        tasksRunning--;
        if (i == SYNC_SUCCESS)
            handler.onSyncSuccess(deviceName);
        else if (i == SYNC_ERROR)
            handler.onSyncError(deviceName);
        else if (i == SYNC_CANCELLED)
            handler.onSyncCancel(deviceName);
    }
}
