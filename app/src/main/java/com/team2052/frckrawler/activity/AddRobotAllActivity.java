package com.team2052.frckrawler.activity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Game;
import com.team2052.frckrawler.database.structures.Team;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class AddRobotAllActivity extends BaseActivity implements OnClickListener {

    private DBManager db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_add_robot_all);

        findViewById(R.id.addRobotsToAll).setOnClickListener(this);
        findViewById(R.id.cancelAddRobots).setOnClickListener(this);

        db = DBManager.getInstance(this);

        Game[] games = db.getAllGames();
        ArrayAdapter<String> gameArray =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        for (Game g : games)
            gameArray.add(g.getName());
        ((Spinner) findViewById(R.id.robotGameSpinner)).setAdapter(gameArray);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addRobotsToAll) {
            String gameName = (String) ((Spinner) findViewById(R.id.robotGameSpinner)).
                    getSelectedItem();
            new AddRobotsTask().execute(gameName);
        } else {
            finish();
        }
    }

    private class AddRobotsTask extends AsyncTask<String, Void, Void> {

        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddRobotAllActivity.this);
            builder.setView(new ProgressSpinner(AddRobotAllActivity.this));
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... gameName) {
            if (gameName.length < 1) {
                Log.e("FRCKrawler", "No robots added. No game name specified.");
                return null;
            }

            Team[] teams = db.getAllTeams();

            for (Team t : teams) {
                if (0 == db.getRobotsByColumns(
                        new String[]{DBContract.COL_GAME_NAME, DBContract.COL_TEAM_NUMBER},
                        new String[]{gameName[0], Integer.toString(t.getNumber())}).length) {
                    db.addRobot(t.getNumber(), gameName[0], null, null, null);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
            finish();
        }
    }
}
