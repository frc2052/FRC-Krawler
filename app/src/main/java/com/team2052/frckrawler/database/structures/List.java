package com.team2052.frckrawler.database.structures;

public class List implements Structure {

    private int eventID;
    private int listID;
    private String name;
    private String description;
    private Robot[] robots;

    public List(int _eventID, String _name, String _description) {
        this(_eventID, _name, _description, new Robot[0]);
    }

    public List(int _eventID, String _name, String _description, Robot[] _robots) {
        this(_eventID, -1, _name, _description, _robots);
    }

    public List(int _eventID, int _listID, String _name, String _description, Robot[] _robots) {
        eventID = _eventID;
        listID = _listID;
        name = _name;
        description = _description;
        robots = _robots;
    }

    public int getEventID() {
        return eventID;
    }

    public int getListID() {
        return listID;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Robot[] getRobots() {
        return robots;
    }
}
