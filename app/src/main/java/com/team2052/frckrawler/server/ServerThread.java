package com.team2052.frckrawler.server;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.team2052.frckrawler.client.ScoutPackage;
import com.team2052.frckrawler.core.BluetoothInfo;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.DaoSession;
import com.team2052.frckrawler.db.Event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class ServerThread extends Thread {

    private final BluetoothAdapter mBluetoothAdapter;
    public boolean isOpen;
    private DaoSession mDaoSession = null;
    private Context context;
    private Event hostedEvent;
    private BluetoothServerSocket serverSocket;

    public ServerThread(ServerService c, Event e) {
        isOpen = false;
        context = c.getApplicationContext();
        hostedEvent = e;
        serverSocket = null;
        mDaoSession = ((FRCKrawler) c.getApplicationContext()).getDaoSession();
        mBluetoothAdapter = Utilities.BluetoothUtil.getBluetoothAdapter();
    }

    @Override
    public void run() {
        //Init the socket
        String deviceName = "device";

        isOpen = true;
        while (isOpen) {
            try {
                serverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (serverSocket != null) {
                long startTime = System.currentTimeMillis();
                try {
                    BluetoothSocket clientSocket = serverSocket.accept();
                    deviceName = clientSocket.getRemoteDevice().getName();
                    //handler.onSyncStart(deviceName);
                    serverSocket.close();
                    ObjectOutputStream toScoutStream = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream fromScoutStream = new ObjectInputStream(clientSocket.getInputStream());
                    BluetoothInfo.ConnectionType connectionType = BluetoothInfo.ConnectionType.VALID_CONNECTION_TYPES[fromScoutStream.readInt()];

                    switch (connectionType) {
                        case SCOUT_SYNC:
                            ((ServerPackage) fromScoutStream.readObject()).save(mDaoSession);
                            toScoutStream.writeObject(new ScoutPackage(mDaoSession, hostedEvent));
                    }

                    toScoutStream.flush();
                    clientSocket.close();
                    //handler.onSyncSuccess(deviceName);
                    Log.d("FRCKrawler", "Synced in: " + (System.currentTimeMillis() - startTime) + "ms");
                } catch (IOException e) {
                    e.printStackTrace();
                    ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(ServerCallbackHandler.SYNC_ONGOING_ID);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeServer() {
        isOpen = false;
        if (serverSocket != null)
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }

    }

}
