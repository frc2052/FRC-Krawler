package com.team2052.frckrawler.core.bluetooth.syncable;

import android.content.Context;

import com.team2052.frckrawler.core.bluetooth.BluetoothConstants;

public class ScoutEventMatchSyncable extends ScoutSyncable {
    public ScoutEventMatchSyncable() {
        super(BluetoothConstants.ReturnCodes.EVENT_MATCH_ERROR);
    }

    @Override
    public void saveToScout(Context context) {

    }
}
