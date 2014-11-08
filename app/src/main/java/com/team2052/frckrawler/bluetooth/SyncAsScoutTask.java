package com.team2052.frckrawler.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.database.Schedule;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class SyncAsScoutTask extends AsyncTask<BluetoothDevice, Void, Integer>
{
    private static int SYNC_SUCCESS = 1;
    private static int SYNC_SERVER_OPEN = 2;
    private static int SYNC_CANCELLED = 3;
    private static int SYNC_ERROR = 4;
    private static int tasksRunning = 0;
    private final DaoSession mDaoSession;

    private volatile String deviceName;
    private Context context;
    private SyncCallbackHandler handler;

    public SyncAsScoutTask(Context c, SyncCallbackHandler h)
    {
        deviceName = "device";
        context = c.getApplicationContext();
        handler = h;
        mDaoSession = ((FRCKrawler) c.getApplicationContext()).getDaoSession();
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
            final List<MatchData> metricMatchData = mDaoSession.getMatchDataDao().loadAll();
            List<PitData> metricPitData = mDaoSession.getPitDataDao().loadAll();
            List<MatchComment> matchComments = mDaoSession.getMatchCommentDao().loadAll();

            if (isCancelled())
                return SYNC_CANCELLED;

            //Write the scout data
            ooStream.writeInt(BluetoothInfo.SCOUT);
            ooStream.writeObject(metricMatchData);
            ooStream.writeObject(metricPitData);
            ooStream.writeObject(matchComments);
            ooStream.flush();


            mDaoSession.runInTx(new Runnable()
            {
                @Override
                public void run()
                {
                    //Delete everything
                    mDaoSession.getGameDao().deleteAll();
                    mDaoSession.getMatchDao().deleteAll();
                    mDaoSession.getRobotDao().deleteAll();
                    mDaoSession.getRobotEventDao().deleteAll();
                    mDaoSession.getMatchDao().deleteAll();
                    mDaoSession.getTeamDao().deleteAll();
                    mDaoSession.getUserDao().deleteAll();
                    mDaoSession.getMetricDao().deleteAll();
                    mDaoSession.getPitDataDao().deleteAll();
                    mDaoSession.getMatchDataDao().deleteAll();
                    mDaoSession.getMatchCommentDao().deleteAll();
                    mDaoSession.getRobotPhotoDao().deleteAll();
                }
            });
            if (isCancelled())
                return SYNC_CANCELLED;

            //Start fdthe reading thread
            //Set the current event id hosted by the server
            final Event event1 = (Event) ioStream.readObject();

            SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, event1.getId());
            editor.apply();

            final List<Metric> inMetric = (List<Metric>) ioStream.readObject();
            final List<User> inUsers = (List<User>) ioStream.readObject();
            final List<RobotEvent> inRobots = (List<RobotEvent>) ioStream.readObject();
            final List<Team> inTeams = (List<Team>) ioStream.readObject();
            final Schedule inSchedule = (Schedule) ioStream.readObject();

            if (isCancelled())
                return SYNC_CANCELLED;

            //Bulk Insert
            mDaoSession.runInTx(new Runnable()
            {
                @Override
                public void run()
                {
                    for (Metric metric : inMetric) {
                        mDaoSession.insertOrReplace(metric);
                    }

                    for (User user : inUsers) {
                        mDaoSession.insertOrReplace(user);
                    }

                    for (RobotEvent robotEvent : inRobots) {
                        mDaoSession.insert(robotEvent);
                        mDaoSession.insertOrReplace(robotEvent.getRobot());
                    }

                    for (Team team : inTeams) {
                        mDaoSession.insertOrReplace(team);
                    }

                    for (Match match : inSchedule.matches) {
                        mDaoSession.insertOrReplace(match);
                    }

                    mDaoSession.insertOrReplace(event1);
                    mDaoSession.insertOrReplace(event1.getGame());
                }
            });


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
