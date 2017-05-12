package com.team2052.frckrawler.bluetooth.syncable;

import android.content.Context;

import com.team2052.frckrawler.bluetooth.BluetoothConstants;

public class ScoutWrongVersionSyncable extends ScoutSyncable {
    public ScoutWrongVersionSyncable() {
        super(BluetoothConstants.ReturnCodes.VERSION_ERROR);
    }

    @Override
    public void saveToScout(Context context) {
        //Do nothing
    }
}
