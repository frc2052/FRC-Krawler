package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Robot;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.RobotListElement;

import java.util.ArrayList;
import java.util.List;

public class RobotListSubscriber extends BaseDataSubscriber<List<Robot>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Robot robot = data.get(i);
            dataToBind.add(new RobotListElement(robot, robot.getGame()));
        }
    }
}
