package com.team2052.frckrawler.listitems.elements;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.databinding.ListItemGameBinding;
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
        ListItemGameBinding binding = DataBindingUtil.inflate(inflater, R.layout.list_item_game, null, false);
        binding.setGame(game);
        return binding.getRoot();
    }

}
