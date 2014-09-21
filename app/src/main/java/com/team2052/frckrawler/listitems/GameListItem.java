package com.team2052.frckrawler.listitems;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.EventsActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.models.Game;

/**
 * Created by Adam on 8/22/2014.
 */
public class GameListItem implements ListItem {
    private final Game game;

    public GameListItem(Game game) {
        this.game = game;
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_game, null);
        ((TextView) convertView.findViewById(R.id.text_game)).setText(game.name);

        final PopupMenu menu = new PopupMenu(c, convertView.findViewById(R.id.edit_game_button));
        menu.getMenu().add(Menu.NONE, R.id.game_edit_event, Menu.NONE, "Events");
        menu.getMenu().add(Menu.NONE, R.id.game_edit_match_metrics, Menu.NONE, "Match Metrics");
        menu.getMenu().add(Menu.NONE, R.id.game_edit_pit_metrics, Menu.NONE, "Pit Metrics");

        convertView.findViewById(R.id.edit_game_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.show();
            }
        });

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.game_edit_event:
                        c.startActivity(EventsActivity.newInstance(c, game));
                        break;
                    case R.id.game_edit_match_metrics:
                        c.startActivity(MetricsActivity.newInstance(c, game, MetricsActivity.MetricType.MATCH_PERF_METRICS));
                        break;
                    case R.id.game_edit_pit_metrics:
                        c.startActivity(MetricsActivity.newInstance(c, game, MetricsActivity.MetricType.ROBOT_METRICS));
                }
                return true;
            }
        });
        convertView.findViewById(R.id.remove_game_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(c);

                builder.setMessage("Are you sure you want to remove this game and all its data?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        game.delete();
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();
            }
        });
        return convertView;
    }

}
