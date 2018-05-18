package com.team2052.frckrawler.core.bluetooth.server;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.Nullable;

import com.team2052.frckrawler.core.bluetooth.BluetoothConnection;
import com.team2052.frckrawler.core.bluetooth.BuildConfig;
import com.team2052.frckrawler.core.bluetooth.syncable.ScoutSyncable;
import com.team2052.frckrawler.core.bluetooth.syncable.ServerSyncable;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class ServerBluetoothConnection extends BluetoothConnection {
    private final int scoutVersion;
    private ServerSyncable serverSyncable;

    public ServerBluetoothConnection(BluetoothSocket socket, ObjectInputStream inputStream, ObjectOutputStream outputStream) throws IOException, ClassNotFoundException {
        super(socket, inputStream, outputStream);
        scoutVersion = getInputStream().readInt();
        if (scoutVersion == BuildConfig.VERSION_CODE) {
            serverSyncable = (ServerSyncable) getInputStream().readObject();
        }
    }

    @Nullable
    public ServerSyncable getServerSyncable() {
        return serverSyncable;
    }

    public boolean isMatchVersionCode() {
        return scoutVersion == BuildConfig.VERSION_CODE;
    }

    public void sendScoutSyncable(ScoutSyncable scoutSyncable) throws IOException, InterruptedException {
        getOutputStreamWrapper().writeObject(scoutSyncable).send();
        Thread.sleep(1000);
        closeConnection();
    }

    public int getScoutVersion() {
        return scoutVersion;
    }
}
