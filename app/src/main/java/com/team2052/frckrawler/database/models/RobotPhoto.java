package com.team2052.frckrawler.database.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.File;
import java.lang.reflect.Field;

/**
 * @author Adam
 * @since 10/4/2014
 */
@Table(name = "robotphoto")
public class RobotPhoto extends Model
{
    @Column(name =  "Robot", onDelete = Column.ForeignKeyAction.CASCADE)
    public Robot robot;

    @Column(name = "File", onDelete = Column.ForeignKeyAction.CASCADE)
    public File file;

    public RobotPhoto(Robot robot, File file)
    {
        this.robot = robot;
        this.file = file;
    }

    public RobotPhoto()
    {
    }
}
