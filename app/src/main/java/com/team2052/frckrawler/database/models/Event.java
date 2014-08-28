package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "Event")
public class Event extends Model {
    @Column(name = "Name")
    public String name;

    @Column(name = "Game")
    public Game game;

    @Column(name = "Date")
    public String date;

    @Column(name = "Location")
    public String location;

    @Column(name = "FMSId")
    public String fmsId;

    public Event(String name, Game game, String date, String location, String fmsId) {
        this.name = name;
        this.game = game;
        this.date = date;
        this.location = location;
        this.fmsId = fmsId;
    }

    public Event() {}
}
