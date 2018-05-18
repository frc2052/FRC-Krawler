package com.team2052.frckrawler.core.bluetooth.syncable;

import android.content.Context;

import com.team2052.frckrawler.core.bluetooth.BluetoothConstants;

import java.io.Serializable;

public abstract class ServerSyncable implements Serializable {
    //Default be scout_sync
    private final int sync_code = BluetoothConstants.SCOUT_SYNC;
    private String event_hash;

    /**
     * Only call constructor from scout device
     */
    public ServerSyncable() {
    }

    public abstract void saveToServer(final Context dbManager);

    public String getEvent_hash() {
        return event_hash;
    }

    protected void setEvent_hash(String event_hash) {
        this.event_hash = event_hash;
    }

    public int getSync_code() {
        return sync_code;
    }
}
