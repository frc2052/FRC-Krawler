package com.team2052.frckrawler.bluetooth.syncable;

import android.content.Context;

import com.team2052.frckrawler.bluetooth.BluetoothConstants;

public class ScoutEventMatchSyncable extends ScoutSyncable {
    public ScoutEventMatchSyncable() {
        super(BluetoothConstants.ReturnCodes.EVENT_MATCH_ERROR);
    }

    @Override
    public void saveToScout(Context context) {

    }
}
