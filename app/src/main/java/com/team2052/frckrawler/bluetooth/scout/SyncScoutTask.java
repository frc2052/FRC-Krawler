package com.team2052.frckrawler.bluetooth.scout;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.bluetooth.BluetoothInfo;
import com.team2052.frckrawler.bluetooth.server.Server;
import com.team2052.frckrawler.database.Schedule;
import com.team2052.frckrawler.db.*;
import com.team2052.frckrawler.events.scout.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncErrorEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncStartEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.util.LogHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import de.greenrobot.event.EventBus;

/**
 * @author Adam, Charlie
 */
public class SyncScoutTask extends AsyncTask<BluetoothDevice, Void, Integer>
{
    private static final int SYNC_SUCCESS = 1;
    private static final int SYNC_SERVER_OPEN = 2;
    private static final int SYNC_CANCELLED = 3;
    private static final int SYNC_ERROR = 4;
    private static int tasksRunning = 0;
    private final DaoSession mDaoSession;

    private volatile String deviceName;
    private Context context;

    public SyncScoutTask(Context c)
    {
        deviceName = "device";
        context = c.getApplicationContext();
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
        EventBus.getDefault().post(new ScoutSyncStartEvent());
    }

    @Override
    protected Integer doInBackground(BluetoothDevice... dev)
    {
        deviceName = dev[0].getName();
        if (Server.getInstance(context).isOpen())
            return SYNC_SERVER_OPEN;
        try {
            LogHelper.info("Syncing With Server");
            LogHelper.info("Syncing with: " + deviceName);
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
            LogHelper.info("Sending data to server to be compiled");
            ooStream.writeInt(BluetoothInfo.SCOUT);
            ooStream.writeObject(metricMatchData);
            ooStream.writeObject(metricPitData);
            ooStream.writeObject(matchComments);
            ooStream.flush();
            LogHelper.info("Data Sent");


            LogHelper.info("Deleting All Data");
            deleteAllData();
            LogHelper.info("Deleted All Data");

            if (isCancelled())
                return SYNC_CANCELLED;

            LogHelper.info("Reading data from server");
            final Event event1 = (Event) ioStream.readObject();
            final List<Metric> inMetric = (List<Metric>) ioStream.readObject();
            final List<User> inUsers = (List<User>) ioStream.readObject();
            final List<RobotEvent> inRobots = (List<RobotEvent>) ioStream.readObject();
            final List<Team> inTeams = (List<Team>) ioStream.readObject();
            final Schedule inSchedule = (Schedule) ioStream.readObject();
            final List<PitData> inPitData = (List<PitData>) ioStream.readObject();

            if (isCancelled())
                return SYNC_CANCELLED;

            //Bulk Insert
            LogHelper.info("Saving data from server");
            insertIntoDatabase(inMetric, inUsers, inRobots, inTeams, inSchedule, event1, inPitData);
            LogHelper.info("Data saved");

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
        LogHelper.info("Done Syncing. Ended with code " + i);
        tasksRunning--;
        if (i == SYNC_SUCCESS)
            EventBus.getDefault().post(new ScoutSyncSuccessEvent());
        else if (i == SYNC_ERROR)
            EventBus.getDefault().post(new ScoutSyncErrorEvent());
        else if (i == SYNC_CANCELLED)
            EventBus.getDefault().post(new ScoutSyncCancelledEvent());
    }

    public void deleteAllData()
    {
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
    }

    public void insertIntoDatabase(final List<Metric> metrics, final List<User> users, final List<RobotEvent> robots, final List<Team> teams, final Schedule schedule, final Event event, final List<PitData> pitData)
    {
        mDaoSession.runInTx(new Runnable()
        {
            @Override
            public void run()
            {
                for (Metric metric : metrics) {
                    mDaoSession.insertOrReplace(metric);
                }

                for (User user : users) {
                    mDaoSession.insertOrReplace(user);
                }

                for (RobotEvent robotEvent : robots) {
                    mDaoSession.insert(robotEvent);
                    mDaoSession.insertOrReplace(robotEvent.getRobot());
                }

                for (Team team : teams) {
                    mDaoSession.insertOrReplace(team);
                }

                for (Match match : schedule.matches) {
                    mDaoSession.insertOrReplace(match);
                }

                for (PitData pitValues : pitData) {
                    mDaoSession.insertOrReplace(pitValues);
                }

                mDaoSession.insertOrReplace(event);
                mDaoSession.insertOrReplace(event.getGame());
            }
        });

        SharedPreferences sharedPreferences = context.getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, event.getId());
        editor.apply();
    }
}
