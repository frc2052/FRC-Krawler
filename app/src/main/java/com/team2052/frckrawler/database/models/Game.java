package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "Games")
public class Game extends Model{
    @Column(name = "Name")
    public String name;

    public Game(String name) {
        this.name = name;
    }

    public Game() {}
}
