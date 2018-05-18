package com.team2052.frckrawler.core.bluetooth.syncable;

import android.content.Context;

import java.io.Serializable;

public abstract class ScoutSyncable implements Serializable {
    private final int return_code;

    public ScoutSyncable(int return_code) {
        this.return_code = return_code;
    }

    public abstract void saveToScout(Context context);

    public int getReturnCode() {
        return return_code;
    }
}
