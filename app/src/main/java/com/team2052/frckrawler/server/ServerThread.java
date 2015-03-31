package com.team2052.frckrawler.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.team2052.frckrawler.client.ScoutPackage;
import com.team2052.frckrawler.core.BluetoothInfo;
import com.team2052.frckrawler.core.FRCKrawler;
import com.team2052.frckrawler.core.database.DBManager;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.Event;

import org.acra.ACRA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.UUID;

public class ServerThread extends Thread {

    private final BluetoothAdapter mBluetoothAdapter;
    private final ServerCallbackHandler handler;
    public boolean isOpen;
    private DBManager mDbManager = null;
    private Context context;
    private Event hostedEvent;
    private BluetoothServerSocket serverSocket;
    public static String TAG = "ServerThread";

    public ServerThread(Context c, long e) {
        isOpen = false;
        context = c;
        mDbManager = ((FRCKrawler) context.getApplicationContext()).getDBSession();
        hostedEvent = mDbManager.getDaoSession().getEventDao().load(e);
        serverSocket = null;
        mBluetoothAdapter = Utilities.BluetoothUtil.getBluetoothAdapter();
        handler = new ServerCallbackHandler(context);

    }


    @Override
    public void run() {
        Log.d(TAG, "run");
        String deviceName = "device";
        BluetoothServerSocket mmServerSocket = null;

        try {
            mmServerSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BluetoothInfo.SERVICE_NAME, UUID.fromString(BluetoothInfo.UUID));
        } catch (IOException e) {
            e.printStackTrace();
            ACRA.getErrorReporter().handleException(e);
        }


        Log.d(TAG, "Loaded socket");
        isOpen = true;
        while (isOpen) {
            if (mmServerSocket != null) {
                BluetoothSocket socket = null;
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    ACRA.getErrorReporter().handleException(e);
                }


                if (socket != null) {
                    deviceName = socket.getRemoteDevice().getName();
                    handler.onSyncStart(deviceName);

                    ObjectInputStream fromScoutStream = null;
                    ObjectOutputStream toScoutStream = null;


                    try {
                        toScoutStream = new ObjectOutputStream(socket.getOutputStream());
                        fromScoutStream = new ObjectInputStream(socket.getInputStream());
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
                                    ServerPackage serverPackage = null;

                                    try {
                                        serverPackage = (ServerPackage) fromScoutStream.readObject();
                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                        ACRA.getErrorReporter().handleException(e);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        ACRA.getErrorReporter().handleException(e);
                                    }

                                    if (serverPackage != null) {
                                        serverPackage.save(mDbManager);
                                    }

                                    try {
                                        toScoutStream.writeObject(new ScoutPackage(mDbManager, hostedEvent));
                                        toScoutStream.flush();
                                        handler.onSyncCancel();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }

                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        ACRA.getErrorReporter().handleException(e);
                    }
                }
            }
        }
    }

    public void closeServer() {
        Log.d(TAG, "closeServer");
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        isOpen = false;
    }

}
