package com.team2052.frckrawler.core.bluetooth.model;

import java.io.Serializable;

public class RobotComment implements Serializable {
    private long robotId;
    private String comment;

    public RobotComment(long robotId, String comment) {
        this.robotId = robotId;
        this.comment = comment;
    }

    public long getRobotId() {
        return robotId;
    }

    public String getComment() {
        return comment;
    }
}
