package com.team2052.frckrawler.bluetooth;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.MatchData;
import com.team2052.frckrawler.database.structures.Metric;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.Schedule;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.database.structures.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ServerThread implements Runnable {

    private boolean open;
    private Context context;
    private DBManager dbManager;
    private Event hostedEvent;
    private SyncCallbackHandler handler;
    private BluetoothServerSocket serverSocket;

    private ServerThread() {
    }

    public ServerThread(Context c, Event e, SyncCallbackHandler h) {
        open = false;
        context = c.getApplicationContext();
        dbManager = DBManager.getInstance(context);
        hostedEvent = e;
        handler = h;
        serverSocket = null;
    }

    @Override
    public void run() {
        String deviceName = "device";
        open = true;
        while (open) {
            try {
                long startTime = System.currentTimeMillis();
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                serverSocket = adapter.listenUsingRfcommWithServiceRecord
                        (BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
                BluetoothSocket clientSocket = serverSocket.accept();
                deviceName = clientSocket.getRemoteDevice().getName();
                handler.onSyncStart(deviceName);
                serverSocket.close();
                //Create the streams
                OutputStream outputStream = clientSocket.getOutputStream();
                ObjectOutputStream oStream = new ObjectOutputStream(outputStream);
                InputStream inputStream = clientSocket.getInputStream();
                ObjectInputStream inStream = new ObjectInputStream(inputStream);
                //Read data
                int connectionType = inStream.readInt();
                if (connectionType == BluetoothInfo.SCOUT) {
                    Event inEvent = (Event) inStream.readObject();
                    Robot[] inRobots = (Robot[]) inStream.readObject();
                    MatchData[] inMatchData = (MatchData[]) inStream.readObject();
                    //Send the received data to the database
                    if (inEvent != null && inEvent.getEventID() ==
                            hostedEvent.getEventID()) {
                        dbManager.updateRobots(inRobots);
                        for (MatchData m : inMatchData)
                            dbManager.insertMatchData(m);
                    }
                    //Compile the data to send
                    //Users
                    User[] usersArr = dbManager.getAllUsers();
                    //Robots
                    Robot[] robotsArr = dbManager.getRobotsAtEvent
                            (hostedEvent.getEventID());
                    //Team Numbers
                    String[] teamNumbers = new String[robotsArr.length];
                    String[] colStrings = new String[robotsArr.length];
                    for (int i = 0; i < teamNumbers.length; i++) {
                        teamNumbers[i] = Integer.toString(robotsArr[i].getTeamNumber());
                        colStrings[i] = DBContract.COL_TEAM_NUMBER;
                    }
                    //Teams
                    Team[] teams = dbManager.getTeamsByColumns(colStrings, teamNumbers, true);
                    String[] teamNames = new String[teams.length];
                    for (int i = 0; i < teams.length; i++) {
                        teamNames[i] = teams[i].getName();
                    }
                    //Metrics
                    Metric[] rMetricsArr = dbManager.getRobotMetricsByColumns(new String[]{DBContract.COL_GAME_NAME},new String[]{hostedEvent.getGameName()});
                    Metric[] mMetricsArr = dbManager.getMatchPerformanceMetricsByColumns(new String[]{DBContract.COL_GAME_NAME}, new String[]{hostedEvent.getGameName()});
                    Schedule schedule = dbManager.getSchedule(hostedEvent.getEventID());
                    //Send data
                    oStream.writeObject(hostedEvent);
                    oStream.writeObject(usersArr);
                    oStream.writeObject(teamNames);
                    oStream.writeObject(robotsArr);
                    oStream.writeObject(rMetricsArr);
                    oStream.writeObject(mMetricsArr);
                    oStream.writeObject(schedule);
                } else {
                    //Sync as a summary
                }
                oStream.flush();
                //Close the socket and notify the sync handler
                clientSocket.close();
                handler.onSyncSuccess(deviceName);
                Log.d("FRCKrawler", "Synced in: " +
                        (System.currentTimeMillis() - startTime) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", "IO error in server.");
                if (!e.getMessage().trim().equals("Operation Canceled") && open) {
                    handler.onSyncError(deviceName);
                }
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", "Sync on ServerThread failed, class not found.");
                System.out.println("Sync error2");
                handler.onSyncError(deviceName);
            }
        }
    }

    public void closeServer() {
        open = false;
        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", "Error closing server.");
            }
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(ServerService.SERVER_OPEN_ID);
        m.cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
    }
}
