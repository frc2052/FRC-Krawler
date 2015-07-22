package com.team2052.frckrawler.bluetooth.server.events;

import com.team2052.frckrawler.db.Event;

/**
 * Created by adam on 3/31/15.
 */
public class ServerStateChangeEvent {
    private final Event event;
    private final boolean state;


    public ServerStateChangeEvent(Event event, boolean state) {
        this.event = event;
        this.state = state;
    }

    public boolean getState() {
        return state;
    }

    public Event getEvent() {
        return event;
    }
}
