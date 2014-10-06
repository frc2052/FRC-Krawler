package com.team2052.frckrawler.database;

import java.io.Serializable;
import java.util.List;

import frckrawler.Event;
import frckrawler.Match;

/**
 * @author Adam
 */
public class Schedule implements Serializable
{
    public Event event;
    public List<Match> matches;

    public Schedule(Event event, List<Match> matches)
    {
        this.event = event;
        this.matches = matches;
    }
}
