package com.team2052.frckrawler.bluetooth.server;

import android.support.annotation.Nullable;

import com.team2052.frckrawler.db.Event;

public class ServerStatus {
    private Event event;
    private boolean status;

    public ServerStatus(Event event, boolean status) {
        this.event = event;
        this.status = status;
    }

    @Nullable
    public Event getEvent() {
        return event;
    }

    public boolean getStatus() {
        return status;
    }
}
