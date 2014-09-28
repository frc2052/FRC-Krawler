package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.*;
import com.team2052.frckrawler.database.DBManager;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "robotevents")
public class RobotEvents extends Model implements Serializable
{
    @Column(name = "Robot", onDelete = Column.ForeignKeyAction.CASCADE)
    public Robot robot;
    @Column(name = "Event", onDelete = Column.ForeignKeyAction.CASCADE)
    public Event event;
    @Column(name = "Attending")
    public boolean isAttending;

    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private int remoteId;

    public RobotEvents(Robot robot, Event event)
    {
        this.remoteId = DBManager.generateRemoteId();
        this.robot = robot;
        this.event = event;
        this.isAttending = true;
    }

    public RobotEvents()
    {
    }

    public void saveAll()
    {
        robot.team.save();
        robot.game.save();
        robot.save();
        event.save();
        save();
    }
}
