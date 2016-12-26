package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamListSubscriber extends BaseDataSubscriber<List<Team>, List<Object>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>(data);
    }
}
