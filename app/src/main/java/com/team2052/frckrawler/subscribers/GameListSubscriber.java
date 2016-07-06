package com.team2052.frckrawler.subscribers;

import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.GameListElement;

import java.util.ArrayList;
import java.util.List;

public class GameListSubscriber extends BaseDataSubscriber<List<Game>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) dataToBind.add(new GameListElement(data.get(i)));
    }
}
