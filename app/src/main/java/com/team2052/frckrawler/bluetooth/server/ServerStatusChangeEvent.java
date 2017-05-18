package com.team2052.frckrawler.bluetooth.server;

import com.team2052.frckrawler.models.Event;

public class ServerStatusChangeEvent {
    private Event event;
    private boolean on;

    public ServerStatusChangeEvent(Event event, boolean on) {
        this.event = event;
        this.on = on;
    }

    public Event getEvent() {
        return event;
    }

    public boolean isOn() {
        return on;
    }
}
