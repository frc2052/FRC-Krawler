package com.team2052.frckrawler.activity.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.models.Team;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.Match;
import com.team2052.frckrawler.database.structures.MetricValue;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.ProgressSpinner;
import com.team2052.frckrawler.tba.readers.EventReader;
import com.team2052.frckrawler.tba.readers.ScheduleReader;
import com.team2052.frckrawler.tba.readers.TeamReader;
import com.team2052.frckrawler.tba.types.TBAEvent;
import com.team2052.frckrawler.tba.types.TBAMatch;
import com.team2052.frckrawler.tba.types.TBATeam;

import java.io.IOException;

public class ImportDialogActivity extends BaseActivity implements OnClickListener {

    public static final String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventIDExtra";

    private DBManager db;
    private Event frckrawlerEvent;
    private TBAEvent[] faEvents;
    private AlertDialog tempDownloadProg;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_import);
        findViewById(R.id.importTeamsAndRobots).setOnClickListener(this);
        findViewById(R.id.importSchedule).setOnClickListener(this);
        findViewById(R.id.cancelImport).setOnClickListener(this);
        db = DBManager.getInstance(this);
        frckrawlerEvent = db.getEventsByColumns(
                new String[]{DBContract.COL_EVENT_ID},
                new String[]{Integer.toString(getIntent().getIntExtra(EVENT_ID_EXTRA, -1))})[0];
        tempDownloadProg = null;
        new ImportFAEventsTask().execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (tempDownloadProg != null)
            tempDownloadProg.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.importTeamsAndRobots:
                final int pos = ((Spinner) findViewById(R.id.eventsSpinner))
                        .getSelectedItemPosition();
                AlertDialog.Builder trBuilder = new AlertDialog.Builder(this);
                trBuilder.setTitle("Are you sure?");
                trBuilder.setMessage("Are you sure you want to import the teams and " +
                        "robots from " + faEvents[pos].getName() +
                        "? Any robot not on the imported list will be removed from " +
                        frckrawlerEvent.getEventName() + " and lose all match data for " +
                        "this event");
                trBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                trBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ImportTeamsAndRobotsTask().execute(faEvents[pos]);
                    }
                });
                trBuilder.show();
                break;

            case R.id.importSchedule:
                final int position = ((Spinner) findViewById(R.id.eventsSpinner))
                        .getSelectedItemPosition();
                AlertDialog.Builder scBuilder = new AlertDialog.Builder(this);
                scBuilder.setTitle("Are you sure?");
                scBuilder
                        .setMessage("Are you sure you want to import the teams and "
                                + "robots from "
                                + faEvents[position].getName()
                                + "? Any robot not on the imported list will be removed from "
                                + frckrawlerEvent.getEventName()
                                + " and lose all match data for " + "this event");
                scBuilder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                );
                scBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new ImportScheduleTask(faEvents[position]).execute();
                            }
                        }
                );
                scBuilder.show();
                break;

            case R.id.cancelImport:
                finish();
                break;
        }
    }

    private void lockScreenOrientation() {
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void releaseScreenOrientation() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    private class ImportFAEventsTask extends AsyncTask<Void, Void, TBAEvent[]> {
        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            lockScreenOrientation();
            AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
            builder.setTitle("Connecting...");
            builder.setView(new ProgressSpinner(ImportDialogActivity.this));
            builder.setCancelable(false);
            progressDialog = tempDownloadProg = builder.create();
            progressDialog.show();
        }

        @Override
        protected TBAEvent[] doInBackground(Void... params) {
            try {
                return new EventReader().readEvents();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", e.getMessage());
            }
            progressDialog.dismiss();
            return new TBAEvent[0];
        }

        @Override
        protected void onPostExecute(TBAEvent[] e) {
            if (e == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
                builder.setTitle("Redirect Error");
                builder.setMessage("The request has been redirected. You probably need to " +
                        "sign in to the network on your browser.");
                builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.show();
            } else if (0 == e.length) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
                builder.setTitle("Connection error!");
                builder.setMessage("There was an in downloading the list of events from " +
                        "the Internet. Check your network connection and try again.");
                builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            } else {
                faEvents = e;
                ArrayAdapter<TBAEvent> adapter = new ArrayAdapter<TBAEvent>(ImportDialogActivity.this,
                        android.R.layout.simple_spinner_item, faEvents);
                ((Spinner) findViewById(R.id.eventsSpinner)).setAdapter(adapter);
                progressDialog.dismiss();
                releaseScreenOrientation();
            }
        }
    }

    private class ImportTeamsAndRobotsTask extends AsyncTask<TBAEvent, Void, Void> {
        private AlertDialog progressDialog;

        @Override
        protected void onPreExecute() {
            lockScreenOrientation();
            AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
            builder.setTitle("Downloading...");
            builder.setView(new ProgressSpinner(ImportDialogActivity.this));
            builder.setCancelable(false);
            progressDialog = tempDownloadProg = builder.create();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(TBAEvent... e) {

            try {
                //Get a list of teams from FIRST Alliance
                TBATeam[] faTeams = new TeamReader(e[0].getID()).readTeams();

                //Remove old robots and their match data from the event
                Robot[] oldRobots = db.getRobotsAtEvent(frckrawlerEvent.getEventID());
                for (Robot r : oldRobots) {
                    boolean isAtEvent = false;
                    for (TBATeam t : faTeams) {
                        if (r.getTeamNumber() == Integer.parseInt(t.getNumber())) {
                            isAtEvent = true;
                            break;
                        }
                    }

                    if (!isAtEvent) {
                        db.removeMatchDataByColumns(
                                new String[]{DBContract.COL_EVENT_ID, DBContract.COL_ROBOT_ID},
                                new String[]{Integer.toString(frckrawlerEvent.getEventID()),
                                        Integer.toString(r.getID())}
                        );
                        db.removeRobotFromEvent(frckrawlerEvent.getEventID(), r.getID());
                    }
                }

                //Create teams that don't exist yet and give them robots
                for (TBATeam t : faTeams) {
                    db.addTeam(Integer.parseInt(t.getNumber()), t.getName(), null, t.getCity(), -1, t.getWebsite(), t.getState(), null);
                    Team team = new Team(Integer.parseInt(t.getNumber()), t.getName(), null, t.getCity(), -1, t.getWebsite(), t.getState(), null);
                    team.save();
                    if (db.getRobotsByColumns(
                            new String[]{DBContract.COL_TEAM_NUMBER, DBContract.COL_GAME_NAME},
                            new String[]{t.getNumber(), frckrawlerEvent.getGameName()})
                            .length < 1) {
                        db.addRobot(Integer.parseInt(t.getNumber()),
                                frckrawlerEvent.getGameName(),
                                "",
                                "",
                                new MetricValue[0]);
                    }

                    //Add the new robot to the FRCKrawler Event
                    Robot r = db.getRobotsByColumns(
                            new String[]{DBContract.COL_TEAM_NUMBER, DBContract.COL_GAME_NAME},
                            new String[]{t.getNumber(), frckrawlerEvent.getGameName()})[0];
                    db.addRobotToEvent(frckrawlerEvent.getEventID(), r.getID());
                }
            } catch (IOException e1) {
                Log.e("FRCKrawler", e1.getMessage() + "");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
            releaseScreenOrientation();
        }
    }

    private class ImportScheduleTask extends AsyncTask<Void, Void, Void> {
        private TBAEvent event;
        private AlertDialog progressDialog;

        public ImportScheduleTask(TBAEvent _event) {
            event = _event;
        }

        @Override
        protected void onPreExecute() {
            lockScreenOrientation();
            AlertDialog.Builder builder = new AlertDialog.Builder(ImportDialogActivity.this);
            builder.setTitle("Downloading...");
            builder.setView(new ProgressSpinner(ImportDialogActivity.this));
            builder.setCancelable(false);
            progressDialog = tempDownloadProg = builder.create();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... v) {
            ScheduleReader reader = new ScheduleReader(event.getID());
            try {
                TBAMatch[] matches = reader.getMatches();
                db.removeAllMatches(frckrawlerEvent.getEventID());
                String game = frckrawlerEvent.getGameName();
                int red1;
                int red2;
                int red3;
                int blue1;
                int blue2;
                int blue3;
                for (int i = 0; i < matches.length; i++) {
                    red1 = matches[i].getAlliances().getRed().getTeam(1);
                    red2 = matches[i].getAlliances().getRed().getTeam(2);
                    red3 = matches[i].getAlliances().getRed().getTeam(3);
                    blue1 = matches[i].getAlliances().getBlue().getTeam(1);
                    blue2 = matches[i].getAlliances().getBlue().getTeam(2);
                    blue3 = matches[i].getAlliances().getBlue().getTeam(3);
                    red1 = getRobotID(game, red1);
                    red2 = getRobotID(game, red2);
                    red3 = getRobotID(game, red3);
                    blue1 = getRobotID(game, blue1);
                    blue2 = getRobotID(game, blue2);
                    blue3 = getRobotID(game, blue3);
                    Log.d("FRCKrawer", red1 + ", " + red2 + ", " + red3);
                    db.addMatch(
                            frckrawlerEvent.getEventID(),
                            new Match(
                                    matches[i].getMatchNumber(),
                                    red1,
                                    red2,
                                    red3,
                                    blue1,
                                    blue2,
                                    blue3,
                                    matches[i].getAlliances().getRed().getScore(),
                                    matches[i].getAlliances().getBlue().getScore()
                            )
                    );
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("FRCKrawler", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
            releaseScreenOrientation();
        }

        private int getRobotID(String game, int teamNumber) {
            Robot[] robots = db.getRobotsByColumns(
                    new String[]{
                            DBContract.COL_TEAM_NUMBER,
                            DBContract.COL_GAME_NAME
                    },
                    new String[]{
                            Integer.toString(teamNumber),
                            game
                    }
            );
            if (robots != null && robots.length > 0) {
                Log.d("FRCKrawer", "ID: " + robots[0].getID());
                return robots[0].getID();
            }
            return -1;
        }
    }
}
