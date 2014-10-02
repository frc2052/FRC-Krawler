package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "robots")
public class Robot extends Model implements Serializable
{
    @Column(name = "Team", onDelete = Column.ForeignKeyAction.CASCADE)
    public Team team;
    @Column(name = "Comments")
    public String comments;
    @Column(name = "Opr")
    public double opr;
    @Column(name = "Game", onDelete = Column.ForeignKeyAction.CASCADE)
    public Game game;
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int remoteId;

    public Robot(Team team, String comments, double opr, Game game)
    {
        this.remoteId = DBManager.generateRemoteId();
        this.team = team;
        this.comments = null;
        this.opr = opr;
        this.game = game;
    }

    public Robot()
    {
    }

    public int setRemoteId()
    {
        remoteId = DBManager.generateRemoteId();
        return remoteId;
    }
}
