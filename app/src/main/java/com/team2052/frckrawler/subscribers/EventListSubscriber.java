package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Event;

import java.util.ArrayList;
import java.util.List;

public class EventListSubscriber extends BaseDataSubscriber<List<Event>, List<Object>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>(data);
    }
}
