package com.team2052.frckrawler.database.subscribers;

import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.elements.GameListElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acorp on 11/17/2015.
 */
public class GameListSubscriber extends BaseDataSubscriber<List<Game>, List<ListItem>> {
    @Override
    public void parseData() {
        dataToBind = new ArrayList<>();
        for (Game game : data) dataToBind.add(new GameListElement(game));
    }
}
