package com.team2052.frckrawler.core.database;

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

    public Schedule(Event event, List<Match> matches) {
        this.event = event;
        this.matches = matches;
    }
}
