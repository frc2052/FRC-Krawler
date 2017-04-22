package com.team2052.frckrawler.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.google.common.base.Strings;
import com.team2052.frckrawler.BuildConfig;
import com.team2052.frckrawler.bluetooth.BluetoothConstants;
import com.team2052.frckrawler.bluetooth.syncable.ScoutDataSyncable;
import com.team2052.frckrawler.bluetooth.syncable.ScoutEventMatchSyncable;
import com.team2052.frckrawler.bluetooth.syncable.ScoutWrongVersionSyncable;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.EventDao;
import com.team2052.frckrawler.db.ServerLogEntry;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.Logger;
import com.team2052.frckrawler.util.Util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.UUID;

public class ServerThread extends Thread {
    public static String TAG = ServerThread.class.getSimpleName();
    private final BluetoothAdapter mBluetoothAdapter;
    private final ServerCallbackHandler handler;
    public boolean isOpen = true;

    private RxDBManager mRxDbManager = null;
    private Context context;
    private Event hostedEvent;
    private BluetoothServerSocket serverSocket;
    String currentSyncDeviceName = "";

    public ServerThread(Context context, Event event) {
        if (event == null) {
            throw new IllegalStateException("Event cannot be null!");
        }

        this.context = context;
        mRxDbManager = RxDBManager.getInstance(context);
        hostedEvent = event;
        serverSocket = null;
        mBluetoothAdapter = BluetoothUtil.getBluetoothAdapter();
        handler = new ServerCallbackHandler(this.context);
    }

    @Override
    public void run() {
        Log.d(TAG, "Server Open");
        isOpen = true;

        //If event doesn't has a unique hash, generate one
        if (Strings.isNullOrEmpty(hostedEvent.getUnique_hash())) {
            hostedEvent.setUnique_hash(Util.generateUniqueHash());
            hostedEvent.update();
        }

        while (isOpen) {
            try {
                serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BluetoothConstants.SERVICE_NAME, UUID.fromString(BluetoothConstants.UUID));
                BluetoothSocket clientSocket = serverSocket.accept();
                serverSocket.close();

                Logger.d(TAG, "Starting sync");
                currentSyncDeviceName = clientSocket.getRemoteDevice().getName();
                handler.onSyncStart(currentSyncDeviceName);

                ObjectOutputStream toScoutStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream fromScoutStream = new ObjectInputStream(clientSocket.getInputStream());

                ServerBluetoothConnection serverBluetoothConnection = new ServerBluetoothConnection(clientSocket, fromScoutStream, toScoutStream);
                handleServerBluetoothConnection(serverBluetoothConnection);

            } catch (Exception e) {
                e.printStackTrace();
                insertLog(String.format("ERROR: %s had an error Syncing with the server"));
                handler.onSyncCancel();

                closeServer();
            }
        }
        Logger.d(TAG, "Server Closed");
    }

    private void handleServerBluetoothConnection(ServerBluetoothConnection connection) throws IOException, InterruptedException {
        if (!connection.isMatchVersionCode()) {
            connection.sendScoutSyncable(new ScoutWrongVersionSyncable());
            insertLog(String.format(
                    "ERROR: %s did not meet server's version requirements the server is running version code #%d and %s is running version code %d", currentSyncDeviceName,
                    BuildConfig.VERSION_CODE,
                    currentSyncDeviceName,
                    connection.getScoutVersion()
            ));
            return;
        }

        if (!Strings.isNullOrEmpty(connection.getServerSyncable().getEvent_hash())) {
            long count = mRxDbManager.getEventsTable().getQueryBuilder().where(EventDao.Properties.Unique_hash.eq(connection.getServerSyncable().getEvent_hash())).count();
            Log.d(TAG, count + " event(s) found with hash");

            if (count == 0) {
                connection.sendScoutSyncable(new ScoutEventMatchSyncable());
                insertLog(String.format("ERROR: Device named %s that is currently synced event did not match this device's unique event id", currentSyncDeviceName));
                return;
            }
        }

        switch (connection.getServerSyncable().getSync_code()) {
            case BluetoothConstants.SCOUT_SYNC:
                connection.sendScoutSyncable(new ScoutDataSyncable(context, hostedEvent));
                insertLog(String.format("INFO: Successfully synced with %s", currentSyncDeviceName));
                handler.onSyncCancel();
                return;
        }

        connection.getServerSyncable().saveToServer(context);
    }


    private void insertLog(String message) {
        Logger.i(TAG, message);
        mRxDbManager.getServerLogEntries().insert(new ServerLogEntry(new Date(), message));
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
