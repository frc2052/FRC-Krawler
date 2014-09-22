package com.team2052.frckrawler.database.models;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "matchcomments")
public class MatchComments {
    @Column(name = "Match")
    public Match match;

    @Column(name = "Robot")
    public Robot robot;

    @Column(name = "Comment")
    public String comment;
}
