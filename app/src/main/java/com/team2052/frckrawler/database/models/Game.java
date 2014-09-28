package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.*;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "games")
public class Game extends Model implements Serializable
{

    @Column(name = "Name")
    public String name;
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int remoteId;

    public Game(String name)
    {
        this.remoteId = DBManager.generateRemoteId();
        this.name = name;
    }

    public Game()
    {
    }
}
