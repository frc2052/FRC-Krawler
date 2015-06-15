package com.team2052.frckrawler.bluetooth.server.events;

/**
 * Created by adam on 3/31/15.
 */
public class ServerStateChangeEvent {
    private final boolean state;

    public ServerStateChangeEvent(boolean state) {
        this.state = state;
    }

    public boolean getState() {
        return state;
    }
}
