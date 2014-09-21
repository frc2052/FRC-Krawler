package com.team2052.frckrawler.activity;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.models.Game;
import com.team2052.frckrawler.database.models.Robot;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.gui.ProgressSpinner;

import java.util.List;

public class AddRobotAllActivity extends BaseActivity implements OnClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_add_robot_all);
        findViewById(R.id.addRobotsToAll).setOnClickListener(this);
        findViewById(R.id.cancelAddRobots).setOnClickListener(this);
        List<Game> games = new Select().from(Game.class).execute();
        ArrayAdapter<String> gameArray = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        for (Game g : games)
            gameArray.add(g.name);
        ((Spinner) findViewById(R.id.robotGameSpinner)).setAdapter(gameArray);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addRobotsToAll) {
            String gameName = (String) ((Spinner) findViewById(R.id.robotGameSpinner)).getSelectedItem();
            new AddRobotsTask().execute((Game) new Select().from(Game.class).where("Name = ?", gameName).executeSingle());
        } else {
            finish();
        }
    }

    private class AddRobotsTask extends AsyncTask<Game, Void, Void> {

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
        protected Void doInBackground(Game... gameName) {
            if (gameName.length < 1) {
                Log.e("FRCKrawler", "No robots added. No game name specified.");
                return null;
            }

            List<Team> teams = new Select().from(Team.class).execute();

            for (Team t : teams) {
                if (0 == new Select().from(Robot.class).where("Team = ?", t.getId()).execute().size()) {
                    new Robot(t, null, -1.0, gameName[0]);
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
