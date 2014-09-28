package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.*;

import java.io.Serializable;

/**
 * @author Adam
 */
@Table(name = "matchcomments")
public class RobotMatchComment extends Model implements Serializable
{
    @Column(name = "Comment")
    public String comment;

    public RobotMatchComment(String comment)
    {
        this.comment = comment;
    }

    public RobotMatchComment()
    {
    }
}
