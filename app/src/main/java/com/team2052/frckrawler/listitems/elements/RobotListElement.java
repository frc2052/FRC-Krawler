package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.listitems.ListElement;

import frckrawler.Robot;

/**
 * @author Adam
 * @since 10/3/2014
 */
public class RobotListElement extends ListElement
{

    private final Robot mRobot;

    public RobotListElement(Robot robot)
    {
        super(Long.toString(robot.getId()));
        this.mRobot = robot;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView)
    {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_robot, null);
        }
        ((TextView) convertView.findViewById(R.id.number)).setText(String.valueOf(mRobot.getTeamId()));
        ((TextView) convertView.findViewById(R.id.game)).setText(mRobot.getGame().getName());
        return convertView;
    }
}
