package com.team2052.frckrawler;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.team2052.frckrawler.activity.TabActivity;
import com.team2052.frckrawler.activity.dialog.EditGameDialogActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;

public class GamesActivity extends TabActivity implements OnClickListener {

    private static final int EDIT_BUTTON_ID = 1;
    private static final int EVENTS_BUTTON_ID = 2;
    private static final int METRICS_BUTTON_ID = 3;

    private DBManager dbManager;
    private AlertDialog metricSelectDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);
        dbManager = DBManager.getInstance(this);
        metricSelectDialog = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetGamesTask().execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (metricSelectDialog != null)
            metricSelectDialog.dismiss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        setNoRootActivitySelected();
    }


    public void postResults(Game[] games) {
        /*TableLayout table = (TableLayout) findViewById(R.id.gamesDataTable);
        table.removeAllViews();

        for (int i = 0; i < games.length; i++) {
            int color;
            if (i % 2 == 0)
                color = GlobalValues.ROW_COLOR;
            else
                color = Color.TRANSPARENT;

            PopupMenuButton menu = new PopupMenuButton(this);
            final String gameName = games[i].getName();
            menu.addItem("Events", new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(GamesActivity.this, EventsActivity.class);
                    intent.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[]
                            {gameName});
                    intent.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[]
                            {gameName});
                    intent.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[]
                            {DBContract.COL_GAME_NAME});
                    startActivity(intent);
                }
            });
            menu.addItem("Metrics", new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GamesActivity.this);
                    LinearLayout l = new LinearLayout(GamesActivity.this);
                    l.setOrientation(LinearLayout.VERTICAL);
                    //Match Metrics Button
                    l.addView(new MyButton(GamesActivity.this, "Match Metrics", new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent i = new Intent(GamesActivity.this, MetricsActivity.class);
                            i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA,
                                    MetricsActivity.MATCH_PERF_METRICS);
                            i.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[]
                                    {gameName});
                            i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[]
                                    {gameName});
                            i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[]
                                    {DBContract.COL_GAME_NAME});
                            startActivity(i);
                        }
                    }));
                    //Robot Metrics Button
                    l.addView(new MyButton(GamesActivity.this, "Robot Metrics", new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            Intent i = new Intent(GamesActivity.this, MetricsActivity.class);
                            i.putExtra(MetricsActivity.METRIC_CATEGORY_EXTRA,
                                    MetricsActivity.ROBOT_METRICS);
                            i.putExtra(StackableTabActivity.PARENTS_EXTRA, new String[]
                                    {gameName});
                            i.putExtra(StackableTabActivity.DB_VALUES_EXTRA, new String[]
                                    {gameName});
                            i.putExtra(StackableTabActivity.DB_KEYS_EXTRA, new String[]
                                    {DBContract.COL_GAME_NAME});
                            startActivity(i);
                        }
                    }));
                    //Cancel Button
                    builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setView(l);
                    metricSelectDialog = builder.create();
                    metricSelectDialog.show();
                }
            });

            MyButton editButton = new MyButton(this, "Edit", this, games[i].getName());
            editButton.setId(EDIT_BUTTON_ID);
            table.addView(new MyTableRow(this, new View[]{
                    menu,
                    editButton,
                    new MyTextView(this, games[i].getName(), 18)
            }, color));
        }*/
    }


    /**
     * **
     * Method: onClick
     * <p/>
     * Summary: This is the listener for the Views on this activity.
     * ***
     */

    @Override
    public void onClick(View v) {

        Intent i;

        switch (v.getId()) {
            case EDIT_BUTTON_ID:
                i = new Intent(this, EditGameDialogActivity.class);
                i.putExtra(EditGameDialogActivity.GAME_NAME_EXTRA, (String) v.getTag());
                startActivity(i);
        }
    }

    private class GetGamesTask extends AsyncTask<Void, Void, Game[]> {

        @Override
        protected Game[] doInBackground(Void... params) {
            return dbManager.getAllGames();
        }

        @Override
        protected void onPostExecute(Game[] games) {
            postResults(games);
        }
    }
}
