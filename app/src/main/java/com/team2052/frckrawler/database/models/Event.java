package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.sql.Date;

/**
 * @author Adam
 */
@Table(name = "events")
public class Event extends Model implements Serializable {
    @Column(name = "Name")
    public String name;

    @Column(name = "Game", onDelete = Column.ForeignKeyAction.CASCADE)
    public Game game;

    @Column(name = "Date")
    public Date date;

    @Column(name = "Location")
    public String location;

    @Column(name = "FMSId")
    public String fmsId;

    public Event(String name, Game game, Date date, String location, String fmsId) {
        this.name = name;
        this.game = game;
        this.date = date;
        this.location = location;
        this.fmsId = fmsId;
    }

    public Event() {}

    @Override
    public String toString() {
        return name + ", " + game.name;
    }
}
