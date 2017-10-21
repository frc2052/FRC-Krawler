package com.team2052.frckrawler.bluetooth.model;

import com.team2052.frckrawler.models.Event;

import java.io.Serializable;

public class Schedule implements Serializable {
    public Event event;

    public Schedule(Event event) {
        this.event = event;
    }
}
