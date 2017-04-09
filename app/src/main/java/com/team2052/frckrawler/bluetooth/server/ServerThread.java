package com.team2052.frckrawler.bluetooth.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.team2052.frckrawler.BuildConfig;
import com.team2052.frckrawler.bluetooth.BluetoothConstants;
import com.team2052.frckrawler.bluetooth.client.ScoutPackage;
import com.team2052.frckrawler.database.RxDBManager;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.ServerLogEntry;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InvalidClassException;
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
        String deviceName;
        isOpen = true;

        //If even't doesn't has a unique hash, generate one
        if (Strings.isNullOrEmpty(hostedEvent.getUnique_hash())) {
            hostedEvent.setUnique_hash(Util.generateUniqueHash());
            hostedEvent.update();
        }

        while (isOpen) {
            try {
                serverSocket = BluetoothAdapter.getDefaultAdapter().listenUsingRfcommWithServiceRecord(BluetoothConstants.SERVICE_NAME, UUID.fromString(BluetoothConstants.UUID));
            } catch (IOException e) {
                if (isOpen) {
                    e.printStackTrace();
                } else {
                    break;
                }
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
                        insertLog(String.format("ERROR: %s had an error Syncing with the server", deviceName));
                    }


                    if (fromScoutStream != null) {
                        Optional<Integer> connection_type = Optional.absent();
                        int version_code = 0;

                        try {
                            version_code = fromScoutStream.readInt();
                        } catch (IOException e) {
                            e.printStackTrace();
                            insertLog(String.format("ERROR: %s had an error Syncing with the server", deviceName));
                        }

                        boolean validVersion = version_code == BuildConfig.VERSION_CODE;

                        try {
                            connection_type = Optional.of(fromScoutStream.readInt());
                        } catch (IOException e) {
                            e.printStackTrace();
                            insertLog(String.format("ERROR: %s had an error Syncing with the server", deviceName));
                        }

                        if (connection_type.isPresent() && validVersion) {
                            switch (connection_type.get()) {
                                case BluetoothConstants.SCOUT_SYNC: {
                                    Log.d(TAG, "Starting sync with Scout");
                                    ServerPackage serverPackage = null;

                                    try {
                                        serverPackage = (ServerPackage) fromScoutStream.readObject();
                                    } catch (ClassNotFoundException | IOException e) {
                                        e.printStackTrace();
                                        insertLog(String.format("ERROR: %s had an error Syncing with the server", deviceName));
                                    }

                                    try {
                                        boolean hashPass = true;
                                        if (serverPackage != null) {
                                            Log.d(TAG, "Checking Event Hashes");
                                            Event scoutEvent = serverPackage.getScoutEvent();

                                            if (scoutEvent != null) {
                                                Event serverEvent = mRxDbManager.getEventsTable().load(scoutEvent.getId());

                                                if (Strings.isNullOrEmpty(scoutEvent.getUnique_hash()) || !scoutEvent.getUnique_hash().equals(serverEvent.getUnique_hash())) {
                                                    Log.d(TAG, "Hash does not pass, sending error back");
                                                    toScoutStream.writeInt(BluetoothConstants.EVENT_MATCH_ERROR);
                                                    toScoutStream.flush();
                                                    handler.onSyncCancel();
                                                    hashPass = false;
                                                    insertLog(String.format("ERROR: Device named %s currently synced event did not match this device's unique event id", deviceName));
                                                }
                                            }

                                            if (hashPass) {
                                                Log.d(TAG, "Sending Data to Scout");
                                                toScoutStream.writeInt(BluetoothConstants.OK);
                                                toScoutStream.writeObject(new ScoutPackage(mRxDbManager, hostedEvent));
                                                toScoutStream.flush();
                                                handler.onSyncCancel();
                                                insertLog(String.format("INFO: Successfully synced with %s", deviceName));
                                            }

                                            if (hashPass) {
                                                Log.d(TAG, "Saving Data from Scout");
                                                serverPackage.save(deviceName, mRxDbManager);
                                            }
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        } else {
                            try {
                                toScoutStream.writeInt(BluetoothConstants.VERSION_ERROR);
                                toScoutStream.writeObject(BuildConfig.VERSION_NAME);
                                toScoutStream.flush();
                                handler.onSyncCancel();

                                insertLog(String.format("ERROR: %s did not meet server's version requirements the server is running version code #%d and %s is running version code %d", deviceName, BuildConfig.VERSION_CODE, deviceName, version_code));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
                        }
                    }
                }
            }
        }
        Log.d(TAG, "Server Closed");
    }

    private void insertLog(String message) {
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
