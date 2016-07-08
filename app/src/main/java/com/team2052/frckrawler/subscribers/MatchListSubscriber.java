package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Match;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.items.MatchListItem;

import java.util.ArrayList;
import java.util.List;

public class MatchListSubscriber extends BaseDataSubscriber<List<Match>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            dataToBind.add(new MatchListItem(data.get(i), true));
        }
    }
}
