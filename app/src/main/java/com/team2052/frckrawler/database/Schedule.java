package com.team2052.frckrawler.database;

import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.Match;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Adam
 */
public class Schedule implements Serializable {
    public Event event;
    public List<Match> matches;

    public Schedule(Event event, List<Match> matches) {
        this.event = event;
        this.matches = matches;
        //Sort by match number
        Collections.sort(matches, new Comparator<Match>() {
            @Override
            public int compare(Match lhs, Match rhs) {
                return Double.compare(lhs.matchNumber, rhs.matchNumber);
            }
        });
    }
}
