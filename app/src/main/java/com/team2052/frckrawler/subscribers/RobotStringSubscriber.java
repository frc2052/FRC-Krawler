package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Robot;

import java.util.ArrayList;
import java.util.List;

public class RobotStringSubscriber extends BaseDataSubscriber<List<Robot>, List<String>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Robot event = data.get(i);
            dataToBind.add(event.getTeam_id() + ", " + event.getTeam().getName());
        }
    }
}
