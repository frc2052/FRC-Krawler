package com.team2052.frckrawler.database.structures;

import java.util.Date;

public class Event implements Structure {

    private int eventID;
    private String eventName;
    private String gameName;
    private Date dateStamp;
    private String location;
    private String fmsID;

    public Event(String _eventName, String _gameName, Date _dateStamp, String _location, String fmsID) {
        this(-1, _eventName, _gameName, _dateStamp, _location, fmsID);
    }

    public Event(int _eventID, String _eventName, String _gameName, Date _dateStamp, String _location, String _fmsID) {
        eventID = _eventID;
        eventName = _eventName;
        gameName = _gameName;
        dateStamp = _dateStamp;
        location = _location;
        fmsID = _fmsID;
    }

    public int getEventID() {
        return eventID;
    }

    public String getEventName() {
        return eventName;
    }

    public String getGameName() {
        return gameName;
    }

    public Date getDateStamp() {
        return dateStamp;
    }

    public String getLocation() {
        return location;
    }

    public String getFMSID() {
        return fmsID;
    }

    @Override
    public String toString() {
        return eventName + ", " + gameName;
    }
}
