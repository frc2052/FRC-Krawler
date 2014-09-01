package com.team2052.frckrawler.bluetooth;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.Schedule;
import com.team2052.frckrawler.database.models.Event;
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

public class ServerThread implements Runnable {

    private boolean open;
    private Context context;
    private Event hostedEvent;
    private SyncCallbackHandler handler;
    private BluetoothServerSocket serverSocket;

    private ServerThread() {
    }

    public ServerThread(Context c, Event e, SyncCallbackHandler h) {
        open = false;
        context = c.getApplicationContext();
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
                serverSocket = adapter.listenUsingRfcommWithServiceRecord(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
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
                    Log.i("FRCKrawler", "Received Scout Sync Read Data From Scout");
                    //TODO RECEIVE
                    Event inEvent = (Event) inStream.readObject();
                    List<MatchData> inMatchData = (List<MatchData>) inStream.readObject();
                    //Send the received data to the database
                    if (inEvent != null && inEvent == hostedEvent) {
                        for (MatchData m : inMatchData)
                            m.save();
                    }
                    //Compile Data To Send
                    Log.i("FRCKrawler", "Received Scout Sync Read Data From Scout");
                    List<User> usersArr = DBManager.loadAllFromType(User.class);
                    List<Metric> metrics = new Select().from(Metric.class).where("Game = ?", hostedEvent.game.getId()).execute();
                    for(Metric metric: metrics){
                        Log.d("FRCKrawler", metric.name);
                    }
                    Schedule schedule = DBManager.genenerateSchedule(hostedEvent);
                    oStream.writeObject(hostedEvent);
                    oStream.writeObject(metrics);
                    oStream.writeObject(usersArr);
                    oStream.writeObject(schedule);
                } else {
                    //Sync as a summary
                }
                oStream.flush();
                //Close the socket and notify the sync handler
                clientSocket.close();
                handler.onSyncSuccess(deviceName);
                Log.d("FRCKrawler", "Synced in: " +(System.currentTimeMillis() - startTime) + "ms");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", "IO error in server.");
                if (!e.getMessage().trim().equals("Operation Canceled") && open) {
                    handler.onSyncError(deviceName);
                }
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeServer() {
        open = false;
        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException e) {}
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.cancel(ServerService.SERVER_OPEN_ID);
        m.cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
    }
}
