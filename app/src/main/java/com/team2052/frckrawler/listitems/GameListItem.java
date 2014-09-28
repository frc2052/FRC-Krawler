package com.team2052.frckrawler.listitems;

import android.content.Context;
import android.view.*;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Game;

/**
 * @author Adam
 */
public class GameListItem extends ListElement
{
    private final Game game;

    public GameListItem(Game game)
    {
        super(Long.toString(game.getId()));
        this.game = game;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView)
    {
        convertView = inflater.inflate(R.layout.list_item_game, null);
        ((TextView) convertView.findViewById(R.id.text_game)).setText(game.name);
        return convertView;
    }

}
