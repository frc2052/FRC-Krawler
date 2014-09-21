package com.team2052.frckrawler.bluetooth;

import java.io.Serializable;

public interface SyncCallbackHandler extends Serializable {

    public void onSyncStart(String deviceName);

    public void onSyncSuccess(String deviceName);

    public void onSyncCancel(String deviceName);

    public void onSyncError(String deviceName);
}
