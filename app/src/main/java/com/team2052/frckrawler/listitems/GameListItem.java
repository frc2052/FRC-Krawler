package com.team2052.frckrawler.listitems;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.EventsActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.fragment.server.GamesFragment;

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

        ((Spinner) convertView.findViewById(R.id.list_view_game_spinner)).setAdapter(ArrayAdapter.createFromResource(c, R.array.game_spinner, android.R.layout.simple_list_item_1));
        ((Spinner) convertView.findViewById(R.id.list_view_game_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("Edit Game")) {
                    return;
                }

                if (selected.equals("Events")) {
                    c.startActivity(EventsActivity.newInstance(c, game));
                    return;
                }
                if (selected.equals("Match Metrics")) {
                    c.startActivity(MetricsActivity.newInstance(c, game, MetricsActivity.MetricType.MATCH_PERF_METRICS));
                    return;
                }

                if (selected.equals("Pit Metrics")) {
                    c.startActivity(MetricsActivity.newInstance(c, game, MetricsActivity.MetricType.ROBOT_METRICS));
                    return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
