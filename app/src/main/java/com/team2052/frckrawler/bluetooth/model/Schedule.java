package com.team2052.frckrawler.bluetooth.model;

import com.team2052.frckrawler.models.Event;
import com.team2052.frckrawler.models.Match;

import java.io.Serializable;
import java.util.List;

public class Schedule implements Serializable {
    public Event event;
    public List<Match> matches;

    public Schedule(Event event) {
        this.event = event;
        event.resetMatchList();
        this.matches = event.getMatchList();
    }
}
