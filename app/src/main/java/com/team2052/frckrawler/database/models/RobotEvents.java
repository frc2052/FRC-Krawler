package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "robotevents")
public class RobotEvents extends Model {
    @Column(name = "Robot", onDelete = Column.ForeignKeyAction.CASCADE)
    public Robot robot;

    @Column(name = "Event", onDelete = Column.ForeignKeyAction.CASCADE)
    public Event event;

    @Column(name = "Attending")
    public boolean isAttending;

    public RobotEvents(Robot robot, Event event, boolean isAttending) {
        this.robot = robot;
        this.event = event;
        this.isAttending = isAttending;
    }

    public RobotEvents() {
    }
}
