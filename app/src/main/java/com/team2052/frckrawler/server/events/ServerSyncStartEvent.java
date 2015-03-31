package com.team2052.frckrawler.server.events;

/**
 * Created by adam on 3/31/15.
 */
public class ServerSyncStartEvent {
    private final String deviceName;
    private final boolean cancel;

    public ServerSyncStartEvent(String deviceName, boolean cancel) {
        this.deviceName = deviceName;
        this.cancel = cancel;
    }
}
