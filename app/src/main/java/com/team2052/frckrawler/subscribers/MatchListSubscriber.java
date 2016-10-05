package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Match;

import java.util.ArrayList;
import java.util.List;

public class MatchListSubscriber extends BaseDataSubscriber<List<Match>, List<Object>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>(data);
    }
}
