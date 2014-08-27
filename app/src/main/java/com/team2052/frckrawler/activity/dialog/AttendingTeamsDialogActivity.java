package com.team2052.frckrawler.activity.dialog;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class AttendingTeamsDialogActivity extends BaseActivity implements OnClickListener {

    public static String GAME_NAME_EXTRA = "com.team2052.frckrawler.gameNameExtra";
    public static String EVENT_ID_EXTRA = "com.team2052.frckrawler.eventIDExtra";

    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_attending_teams);

        findViewById(R.id.save).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);

        dbManager = DBManager.getInstance(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetRobotsTask().execute();
    }

    public void postResults(Robot[] allRobots, Robot[] selectedRobots) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.save) {
            LinearLayout robotList = (LinearLayout) findViewById(R.id.teamList);

            boolean[] checkBoxVals = new boolean[robotList.getChildCount()];
            int[] robotIDs = new int[robotList.getChildCount()];

            for (int currentChild = 0; currentChild < robotList.getChildCount(); currentChild++) {
                CheckBox box = (CheckBox) robotList.getChildAt(currentChild);
                checkBoxVals[currentChild] = box.isChecked();
                robotIDs[currentChild] = box.getId();
            }

            new SaveAttendingRobotsTask(checkBoxVals, robotIDs).execute();

        } else if (v.getId() == R.id.cancel) {
            finish();
        }
    }

    private class SaveAttendingRobotsTask extends AsyncTask<Void, Void, Void> {

        private boolean[] checkBoxVals;
        private int[] robotIDs;
        private AlertDialog progressDialog;

        public SaveAttendingRobotsTask(boolean[] _checkBoxVals, int[] _robotIDs) {
            checkBoxVals = _checkBoxVals;
            robotIDs = _robotIDs;
        }

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(AttendingTeamsDialogActivity.this);
            builder.setTitle("Saving...");
            builder.setView(new ProgressSpinner(AttendingTeamsDialogActivity.this));
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; i < robotIDs.length; i++) {
                if (checkBoxVals[i])
                    dbManager.addRobotToEvent(Integer.parseInt(getIntent().getStringExtra(EVENT_ID_EXTRA)), robotIDs[i]);
                else
                    dbManager.removeRobotFromEvent(Integer.parseInt(getIntent().getStringExtra(EVENT_ID_EXTRA)), robotIDs[i]);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
            finish();
        }
    }

    private class GetRobotsTask extends AsyncTask<Void, Void, Robot[]> {

        private Robot[] allRobots;
        private LinearLayout robotList;

        @Override
        protected void onPreExecute() {
            robotList = (LinearLayout) findViewById(R.id.teamList);
            robotList.removeAllViews();
            robotList.addView(new ProgressSpinner(AttendingTeamsDialogActivity.this));
        }

        @Override
        protected Robot[] doInBackground(Void... params) {
            return dbManager.getRobotsByColumns(new String[]{DBContract.COL_GAME_NAME}, new String[]{getIntent().getStringExtra(GAME_NAME_EXTRA)});
        }

        @Override
        protected void onPostExecute(Robot[] _allRobots) {
            allRobots = _allRobots;
            new GetSelectedRobotsTask().execute();
        }

        private class GetSelectedRobotsTask extends AsyncTask<Void, Void, Robot[]> {

            @Override
            protected Robot[] doInBackground(Void... params) {
                return dbManager.getRobotsAtEvent(Integer.parseInt(getIntent().getStringExtra(EVENT_ID_EXTRA)));
            }

            @Override
            protected void onPostExecute(Robot[] selectedRobots) {
                robotList.removeAllViews();

                for (Robot r : allRobots) {
                    CheckBox checkBox = new CheckBox(AttendingTeamsDialogActivity.this);
                    checkBox.setId(r.getID());
                    checkBox.setText(Integer.toString(r.getTeamNumber()));

                    for (Robot sr : selectedRobots)
                        if (r.getID() == sr.getID())
                            checkBox.setChecked(true);

                    robotList.addView(checkBox);
                }
            }
        }
    }
}
