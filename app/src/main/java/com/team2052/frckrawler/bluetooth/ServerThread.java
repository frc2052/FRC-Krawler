package com.team2052.frckrawler.bluetooth;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.Schedule;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Metric;
import com.team2052.frckrawler.database.models.MetricMatchData;
import com.team2052.frckrawler.database.models.RobotEvents;
import com.team2052.frckrawler.database.models.User;
import com.team2052.frckrawler.util.LogHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

public class ServerThread extends Thread
{

    private boolean isOpen;
    private Context context;
    private Event hostedEvent;
    private SyncCallbackHandler handler;
    private BluetoothServerSocket serverSocket;

    private ServerThread()
    {
    }

    public ServerThread(Context c, Event e, SyncCallbackHandler h)
    {
        isOpen = false;
        context = c.getApplicationContext();
        hostedEvent = e;
        handler = h;
        serverSocket = null;

    }

    @Override
    public void run()
    {
        //Init the socket
        String deviceName = "device";

        isOpen = true;
        while (isOpen) {
            try {
                LogHelper.debug("Init ServerSocket");
                serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
                LogHelper.debug("ServerSocket OK");
            } catch (IOException io) {
                LogHelper.debug("ServerSocket ERROR");
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
                handler.onSyncStart(deviceName);

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
                    List<MetricMatchData> inMetricMatchData = (List<MetricMatchData>) inStream.readObject();

                    ActiveAndroid.beginTransaction();

                    //Save all the data
                    for (MetricMatchData m : inMetricMatchData) {
                        m.robot.save();
                        m.metric.save();
                        m.match.saveAll();
                        m.save();
                    }

                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();


                    ActiveAndroid.beginTransaction();

                    //Compile Data To Send
                    List<User> usersArr = DBManager.loadAllFromType(User.class);
                    List<Metric> metrics = new Select().from(Metric.class).where("Game = ?", hostedEvent.game.getId()).execute();
                    List<RobotEvents> robots = new Select().from(RobotEvents.class).where("Event = ?", hostedEvent.getId()).and("Attending = ?", true).execute();
                    Schedule schedule = DBManager.genenerateSchedule(hostedEvent);

                    ActiveAndroid.setTransactionSuccessful();
                    ActiveAndroid.endTransaction();

                    //Write the objects to
                    oStream.writeObject(hostedEvent);
                    oStream.writeObject(metrics);
                    oStream.writeObject(usersArr);
                    oStream.writeObject(robots);
                    oStream.writeObject(schedule);
                }

                oStream.flush();
                clientSocket.close();
                handler.onSyncSuccess(deviceName);
                Log.d("FRCKrawler", "Synced in: " + (System.currentTimeMillis() - startTime) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", "IO error in server.");
                if (!e.getMessage().trim().equals("Operation Canceled") && isOpen) {
                    handler.onSyncError(deviceName);
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
