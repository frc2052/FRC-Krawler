package com.team2052.frckrawler.database;

import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.db.Match;

import java.io.Serializable;
import java.util.List;

/**
 * @author Adam
 *         Compact way of a match list
 */
public class Schedule implements Serializable {
    public Event event;
    public List<Match> matches;

    public Schedule(Event event) {
        this.event = event;
        event.resetMatchList();
        this.matches = event.getMatchList();
    }
}
