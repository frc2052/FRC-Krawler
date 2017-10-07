package com.team2052.frckrawler.adapters.items.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.items.ListElement;
import com.team2052.frckrawler.data.RxDBManager;
import com.team2052.frckrawler.models.Robot;
import com.team2052.frckrawler.models.Season;

/**
 * @author Adam
 * @since 10/3/2014
 */
public class RobotListElement extends ListElement {

    private final Robot mRobot;
    private Season season;

    public RobotListElement(Robot robot, Season season) {
        super(Long.toString(robot.getId()));
        this.mRobot = robot;
        this.season = season;
    }

    @Override
    public View getView(Context c, LayoutInflater inflater, View convertView) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_robot, null);
        }
        ((TextView) convertView.findViewById(R.id.number)).setText("Team " + String.valueOf(mRobot.getTeam_id()));
        ((TextView) convertView.findViewById(R.id.game)).setText(season.getName() + " - " + RxDBManager.Companion.getInstance(c).getRobotsTable().getTeam(mRobot).getName());
        return convertView;
    }
}
