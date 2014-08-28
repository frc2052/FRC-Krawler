package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * @author Adam
 */
@Table(name = "Users")
public class User extends Model {
    @Column(name = "Name")
    public String name;

    public User(String name) {
        this.name = name;
    }

    public User() {}
}
