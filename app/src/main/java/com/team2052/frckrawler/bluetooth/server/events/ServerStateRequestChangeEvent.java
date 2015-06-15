package com.team2052.frckrawler.bluetooth.server.events;

import com.team2052.frckrawler.db.Event;

/**
 * Created by Adam on 6/12/2015.
 */
public class ServerStateRequestChangeEvent {
    boolean state;
    Event event;

    public ServerStateRequestChangeEvent(boolean state, Event event) {
        this.state = state;
        this.event = event;
    }

    public ServerStateRequestChangeEvent(boolean state) {
        this.state = state;
    }

    public boolean getRequestedState() {
        return state;
    }

    public Event getEvent() {
        return event;
    }
}
