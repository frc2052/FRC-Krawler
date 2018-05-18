package com.team2052.frckrawler.core.bluetooth.scout.events;

/**
 * @author Adam
 * @since 12/5/2014.
 */
public class ScoutSyncErrorEvent {
    public String message = null;

    public ScoutSyncErrorEvent() {
    }

    public ScoutSyncErrorEvent(String message) {
        this.message = message;
    }
}
