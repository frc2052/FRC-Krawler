package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.listitems.ListElement;

/**
 * @author Adam
 * @since 10/3/2014
 */
public class RobotListElement extends ListElement
{

    public RobotListElement(Robot robot)
    {
        super(Long.toString(robot.getId()));
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView)
    {
        return convertView;
    }
}
