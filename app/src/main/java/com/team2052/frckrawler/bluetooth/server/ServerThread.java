package com.team2052.frckrawler.bluetooth.server;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.team2052.frckrawler.FRCKrawler;
import com.team2052.frckrawler.bluetooth.BluetoothInfo;
import com.team2052.frckrawler.database.Schedule;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.MatchComment;
import com.team2052.frckrawler.db.MatchCommentDao;
import com.team2052.frckrawler.db.MatchDao;
import com.team2052.frckrawler.db.MatchData;
import com.team2052.frckrawler.db.MatchDataDao;
import com.team2052.frckrawler.db.Metric;
import com.team2052.frckrawler.db.MetricDao;
import com.team2052.frckrawler.db.PitData;
import com.team2052.frckrawler.db.PitDataDao;
import com.team2052.frckrawler.db.RobotEvent;
import com.team2052.frckrawler.db.RobotEventDao;
import com.team2052.frckrawler.db.Team;
import com.team2052.frckrawler.db.User;
import com.team2052.frckrawler.util.BluetoothUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ServerThread extends Thread
{

    private final BluetoothAdapter mBluetoothAdapter;
    private final Server server;
    private DaoSession mDaoSession = null;
    private boolean isOpen;
    private Context context;
    private Event hostedEvent;
    private BluetoothServerSocket serverSocket;

    public ServerThread(ServerService c, Event e)
    {
        isOpen = false;
        context = c.getApplicationContext();
        hostedEvent = e;
        serverSocket = null;
        server = Server.getInstance(c);
        mDaoSession = ((FRCKrawler) c.getApplicationContext()).getDaoSession();
        mBluetoothAdapter = BluetoothUtil.getBluetoothAdapter();
    }

    @Override
    public void run()
    {
        //Init the socket
        String deviceName = "device";

        isOpen = true;
        while (isOpen) {
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
            } catch (IOException io) {
                io.printStackTrace();
            }

            try {
                //Set current time
                long startTime = System.currentTimeMillis();

                //Accept the client socket
                BluetoothSocket clientSocket = serverSocket.accept();

                //Get the device name
                deviceName = clientSocket.getRemoteDevice().getName();

                //Call the sync handler
                //handler.onSyncStart(deviceName);

                //Close the server socket
                serverSocket.close();

                //Create the streams
                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectOutputStream oStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream inStream = new ObjectInputStream(inputStream);

                //Read the type of sync
                int connectionType = inStream.readInt();

                if (connectionType == BluetoothInfo.SCOUT) {
                    //Get the data from the stream
                    final List<MatchData> inMatchData = (List<MatchData>) inStream.readObject();
                    final List<PitData> inPitData = (List<PitData>) inStream.readObject();
                    final List<MatchComment> inMatchComments = (List<MatchComment>) inStream.readObject();

                    mDaoSession.runInTx(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //Save all the data
                            for (MatchData m : inMatchData) {
                                //Don't Save data if it already exists.
                                if (mDaoSession.getMatchDataDao().queryBuilder().where(MatchDataDao.Properties.RobotId.eq(m.getRobotId())).where(MatchDataDao.Properties.MetricId.eq(m.getMetricId())).where(MatchDataDao.Properties.MatchId.eq(m.getMatchId())).list().size() <= 0)
                                    mDaoSession.getMatchDataDao().insertOrReplace(m);
                            }

                            for (PitData m : inPitData) {
                                //Don't Save data if it already exists.
                                if (mDaoSession.getPitDataDao().queryBuilder().where(PitDataDao.Properties.RobotId.eq(m.getRobotId())).where(PitDataDao.Properties.MetricId.eq(m.getMetricId())).list().size() <= 0)
                                    mDaoSession.getPitDataDao().insertOrReplace(m);
                            }

                            for (MatchComment matchComment : inMatchComments) {
                                //Don't Save data if it already exists.
                                if (mDaoSession.getMatchCommentDao().queryBuilder().where(MatchCommentDao.Properties.MatchId.eq(matchComment.getMatchId())).where(MatchCommentDao.Properties.RobotId.eq(matchComment.getRobotId())).list().size() <= 0)
                                    mDaoSession.getMatchCommentDao().insert(matchComment);
                            }

                        }
                    });

                    //Compile Data To Send
                    List<User> usersArr = mDaoSession.getUserDao().loadAll();
                    List<Metric> metrics = mDaoSession.getMetricDao().queryDeep("WHERE " + MetricDao.Properties.GameId.columnName + " = " + hostedEvent.getGame().getId());
                    List<RobotEvent> robots = mDaoSession.getRobotEventDao().queryDeep("WHERE " + RobotEventDao.Properties.EventId.columnName + " = " + hostedEvent.getId());
                    Schedule schedule = new Schedule(hostedEvent, mDaoSession.getMatchDao().queryDeep("WHERE " + MatchDao.Properties.EventId.columnName + " = " + hostedEvent.getId()));
                    List<Team> teams = new ArrayList<>();

                    for (RobotEvent robotEvent : robots) {
                        teams.add(robotEvent.getRobot().getTeam());
                    }

                    //Write the objects to OStream
                    oStream.writeObject(hostedEvent);
                    oStream.writeObject(metrics);
                    oStream.writeObject(usersArr);
                    oStream.writeObject(robots);
                    oStream.writeObject(teams);
                    oStream.writeObject(schedule);
                }

                oStream.flush();
                clientSocket.close();
                //handler.onSyncSuccess(deviceName);
                Log.d("FRCKrawler", "Synced in: " + (System.currentTimeMillis() - startTime) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", "IO error in server.");
                if (!e.getMessage().trim().equals("Operation Canceled") && isOpen) {
                    //handler.onSyncError(deviceName);
                }
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeServer()
    {
        isOpen = false;
        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(ServerService.SERVER_OPEN_ID);
        m.cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
    }
}
