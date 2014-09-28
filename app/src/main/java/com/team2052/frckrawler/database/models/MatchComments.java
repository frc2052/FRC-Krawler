package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.*;

/**
 * @author Adam
 */
@Table(name = "matchcomments")
public class MatchComments extends Model
{
    @Column(name = "Match")
    public Match match;

    @Column(name = "Robot")
    public Robot robot;

    @Column(name = "Comment")
    public String comment;

    public MatchComments(Match match, Robot robot, String comment)
    {
        this.match = match;
        this.robot = robot;
        this.comment = comment;
    }

    public MatchComments()
    {
    }
}
