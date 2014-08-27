package com.team2052.frckrawler.listitems;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.EventsActivity;
import com.team2052.frckrawler.activity.MetricsActivity;
import com.team2052.frckrawler.activity.StackableTabActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;
import com.team2052.frckrawler.fragment.GamesFragment;

/**
 * Created by Adam on 8/22/2014.
 */
public class GameListItem implements ListItem {
    private final String name;
    private final GamesFragment fragment;

    public GameListItem(String name, GamesFragment fragment) {
        this.fragment = fragment;
        this.name = name;
    }

    public GameListItem(Game game, GamesFragment fragment) {
        this(game.getName(), fragment);
    }

    @Override
    public View getView(final Context c, LayoutInflater inflater, View convertView) {
        convertView = inflater.inflate(R.layout.list_item_game, null);
        ((TextView) convertView.findViewById(R.id.text_game)).setText(name);

        ((Spinner) convertView.findViewById(R.id.list_view_game_spinner)).setAdapter(ArrayAdapter.createFromResource(c, R.array.game_spinner, android.R.layout.simple_list_item_1));
        ((Spinner) convertView.findViewById(R.id.list_view_game_spinner)).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                if (selected.equals("Edit Game")) {
                    return;
                }

                if (selected.equals("Events")) {
                    Intent intent = new Intent(c, EventsActivity.class);
                    intent.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[]{name});
                    intent.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[]{name});
                    intent.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[]{DBContract.COL_GAME_NAME});
                    c.startActivity(intent);
                    return;
                }
                if (selected.equals("Match Metrics")) {
                    Intent i = new Intent(c, MetricsActivity.class);
                    i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA, MetricsActivity.MATCH_PERF_METRICS);
                    i.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[]{GameListItem.this.name});
                    i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[]{GameListItem.this.name});
                    i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[]{DBContract.COL_GAME_NAME});
                    c.startActivity(i);
                    return;
                }

                if (selected.equals("Pit Metrics")) {
                    Intent i = new Intent(c, MetricsActivity.class);
                    i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA, MetricsActivity.ROBOT_METRICS);
                    i.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[]{GameListItem.this.name});
                    i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[]{GameListItem.this.name});
                    i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[]{DBContract.COL_GAME_NAME});
                    c.startActivity(i);
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
                        DBManager.getInstance(c).removeGame(GameListItem.this.name);
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
