package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.db.Game;
import com.team2052.frckrawler.listitems.ListElement;

/**
 * @author Adam
 */
public class GameListElement extends ListElement {
    private final Game game;

    public GameListElement(Game game) {
        super(Long.toString(game.getId()));
        this.game = game;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_game, null, false);
        ((TextView) convertView.findViewById(R.id.game_name)).setText(game.getName());
        return convertView;
    }

}
