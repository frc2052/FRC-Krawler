package com.team2052.frckrawler.bluetooth;

import com.team2052.frckrawler.db.Event;

import java.io.Serializable;
import java.util.List;

public class Schedule implements Serializable {
    public Event event;

    public Schedule(Event event) {
        this.event = event;
    }
}
