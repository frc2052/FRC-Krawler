package com.team2052.frckrawler.database.structures;

public class Schedule implements Structure {
    private int eventID;
    private Match[] matches;

    public Schedule(int _eventID, Match[] _matches) {
        eventID = _eventID;
        matches = _matches;
    }

    public Match getMatch(int matchNum) {
        return matches[matchNum - 1];
    }

    public Match[] getAllMatches() {
        return matches;
    }

    public int getNumberMatches() {
        return matches.length;
    }

    public int getEventID() {
        return eventID;
    }
}
