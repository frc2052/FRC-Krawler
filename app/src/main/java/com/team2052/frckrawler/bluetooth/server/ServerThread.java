package com.team2052.frckrawler.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.team2052.frckrawler.bluetooth.BluetoothInfo;
import com.team2052.frckrawler.bluetooth.ServerPackage;
import com.team2052.frckrawler.bluetooth.client.ScoutPackage;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.util.BluetoothUtil;

import org.acra.ACRA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class ServerThread extends Thread {

    public static String TAG = ServerThread.class.getSimpleName();
    private final BluetoothAdapter mBluetoothAdapter;
    private final ServerCallbackHandler handler;
    public boolean isOpen = true;

    private DBManager mDbManager = null;
    private Context context;
    private Event hostedEvent;
    private BluetoothServerSocket serverSocket;

    public ServerThread(Context context, Event event) {
        this.context = context;
        mDbManager = DBManager.getInstance(context);
        hostedEvent = event;
        serverSocket = null;
        mBluetoothAdapter = BluetoothUtil.getBluetoothAdapter();
        handler = new ServerCallbackHandler(this.context);
    }

    @Override
    public void run() {
        Log.d(TAG, "Server Open");
        String deviceName = "device";
        isOpen = true;
        while (isOpen) {
            try {
                serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
            } catch (IOException e) {
                e.printStackTrace();
                ACRA.getErrorReporter().handleException(e);
            }

            if (serverSocket != null) {
                BluetoothSocket clientSocket = null;

                try {
                    clientSocket = serverSocket.accept();
                    serverSocket.close();
                } catch (IOException ignored) {
                }

                if (clientSocket != null) {
                    Log.d(TAG, "Starting sync");
                    deviceName = clientSocket.getRemoteDevice().getName();
                    handler.onSyncStart(deviceName);

                    ObjectInputStream fromScoutStream = null;
                    ObjectOutputStream toScoutStream = null;

                    try {
                        toScoutStream = new ObjectOutputStream(clientSocket.getOutputStream());
                        fromScoutStream = new ObjectInputStream(clientSocket.getInputStream());
                    } catch (IOException e) {
                        e.printStackTrace();
                        ACRA.getErrorReporter().handleException(e);
                    }


                    if (fromScoutStream != null) {
                        BluetoothInfo.ConnectionType connection_type = null;

                        try {
                            connection_type = BluetoothInfo.ConnectionType.VALID_CONNECTION_TYPES[fromScoutStream.readInt()];
                        } catch (IOException e) {
                            e.printStackTrace();
                            ACRA.getErrorReporter().handleException(e);
                        }

                        if (connection_type != null) {

                            switch (connection_type) {
                                case SCOUT_SYNC: {
                                    Log.d(TAG, "Starting sync with Scout");
                                    ServerPackage serverPackage = null;

                                    try {
                                        serverPackage = (ServerPackage) fromScoutStream.readObject();
                                    } catch (ClassNotFoundException | IOException e) {
                                        e.printStackTrace();
                                        ACRA.getErrorReporter().handleException(e);
                                    }

                                    if (serverPackage != null) {
                                        Log.d(TAG, "Saving Data from Scout");
                                        serverPackage.save(mDbManager);
                                    }

                                    try {
                                        Log.d(TAG, "Sending Data to Scout");
                                        toScoutStream.writeObject(new ScoutPackage(mDbManager, hostedEvent));
                                        toScoutStream.flush();
                                        handler.onSyncCancel();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    //Sleep to keep stream open
                                    //Some random bug in Android
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ignored) {
                                    }

                                    try {
                                        clientSocket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ACRA.getErrorReporter().handleException(e);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Server Closed");
    }

    public void closeServer() {
        isOpen = false;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
            }
        }
    }

    public Event getHostedEvent() {
        return hostedEvent;
    }
}
