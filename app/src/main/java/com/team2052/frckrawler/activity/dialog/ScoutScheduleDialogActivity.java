package com.team2052.frckrawler.activity.dialog;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Match;
import com.team2052.frckrawler.database.structures.Robot;
import com.team2052.frckrawler.database.structures.Schedule;
import com.team2052.frckrawler.gui.MyTableRow;
import com.team2052.frckrawler.gui.MyTextView;
import com.team2052.frckrawler.gui.ProgressSpinner;
import com.team2052.frckrawler.gui.StaticTableLayout;

public class ScoutScheduleDialogActivity extends BaseActivity implements OnClickListener {
    private StaticTableLayout dataTable;
    private DBManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_scout_schedule);
        findViewById(R.id.close).setOnClickListener(this);
        dataTable = (StaticTableLayout) findViewById(R.id.scheduleTable);
        db = DBManager.getInstance(this);
        new GetScheduleTask().execute();
    }

    @Override
    public void onClick(View v) {
        finish();
    }

    private class GetScheduleTask extends AsyncTask<Void, MyTableRow, Void> {
        AlertDialog progressDialog;
        Robot[] robots;

        @Override
        protected void onPreExecute() {
            AlertDialog.Builder builder = new AlertDialog.Builder(ScoutScheduleDialogActivity.this);
            builder.setTitle("Loading...");
            builder.setView(new ProgressSpinner(ScoutScheduleDialogActivity.this));
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... v) {
            robots = db.scoutGetAllRobots();
            Schedule schedule = db.scoutGetSchedule();
            MyTableRow staticDesRow = new MyTableRow(
                    ScoutScheduleDialogActivity.this,
                    new View[]{
                            new MyTextView(ScoutScheduleDialogActivity.this, "Match #", 18)
                    }
            );
            MyTableRow desRow = new MyTableRow(
                    ScoutScheduleDialogActivity.this,
                    new View[]{
                            new MyTextView(ScoutScheduleDialogActivity.this, "Red 1", 18),
                            new MyTextView(ScoutScheduleDialogActivity.this, "Red 2", 18),
                            new MyTextView(ScoutScheduleDialogActivity.this, "Red 3", 18),
                            new MyTextView(ScoutScheduleDialogActivity.this, "Blue 1", 18),
                            new MyTextView(ScoutScheduleDialogActivity.this, "Blue 2", 18),
                            new MyTextView(ScoutScheduleDialogActivity.this, "Blue 3", 18),
                            new MyTextView(ScoutScheduleDialogActivity.this, "Red Score", 18),
                            new MyTextView(ScoutScheduleDialogActivity.this, "Blue Score", 18)
                    }
            );
            desRow.setLayoutParams(new TableLayout.LayoutParams());
            publishProgress(staticDesRow, desRow);
            for (int i = 0; i < schedule.getAllMatches().length; i++) {
                int color;
                if (i % 2 == 0)
                    color = GlobalValues.ROW_COLOR;
                else
                    color = Color.TRANSPARENT;
                Match match = schedule.getAllMatches()[i];
                //Get the team numbers
                String red1 = getRobotTeamNum(match.getRed1RobotID());
                String red2 = getRobotTeamNum(match.getRed2RobotID());
                String red3 = getRobotTeamNum(match.getRed3RobotID());
                String blue1 = getRobotTeamNum(match.getBlue1RobotID());
                String blue2 = getRobotTeamNum(match.getBlue2RobotID());
                String blue3 = getRobotTeamNum(match.getBlue3RobotID());
                //Set up the match scores
                int redScoreInt = match.getRedScore();
                String redScore = Integer.toString(redScoreInt);
                if (-1 == match.getRedScore())
                    redScore = " ";
                int blueScoreInt = match.getBlueScore();
                String blueScore = Integer.toString(blueScoreInt);
                if (-1 == match.getBlueScore())
                    blueScore = " ";
                //Set up the match number String
                String matchNumLabel = Integer.toString(match.getMatchNumber());
                if (-1 != redScoreInt && -1 != blueScoreInt) {
                    if (redScoreInt == blueScoreInt) {
                        matchNumLabel += " (D)";
                    } else if (redScoreInt > blueScoreInt) {
                        matchNumLabel += " (R)";
                    } else {
                        matchNumLabel += " (B)";
                    }
                }
                //Create the static and main table rows
                MyTableRow statRow = new MyTableRow(
                        ScoutScheduleDialogActivity.this,
                        new View[]{
                                new MyTextView(ScoutScheduleDialogActivity.this, matchNumLabel, 18)
                        }, color
                );
                MyTableRow mainRow = new MyTableRow(
                        ScoutScheduleDialogActivity.this,
                        new View[]{
                                new MyTextView(ScoutScheduleDialogActivity.this, red1, 18),
                                new MyTextView(ScoutScheduleDialogActivity.this, red2, 18),
                                new MyTextView(ScoutScheduleDialogActivity.this, red3, 18),
                                new MyTextView(ScoutScheduleDialogActivity.this, blue1, 18),
                                new MyTextView(ScoutScheduleDialogActivity.this, blue2, 18),
                                new MyTextView(ScoutScheduleDialogActivity.this, blue3, 18),
                                new MyTextView(ScoutScheduleDialogActivity.this, redScore, 18),
                                new MyTextView(ScoutScheduleDialogActivity.this, blueScore, 18)
                        }, color
                );
                publishProgress(statRow, mainRow);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(MyTableRow... rows) {
            dataTable.addViewToStaticTable(rows[0]);
            dataTable.addViewToMainTable(rows[1]);
        }

        @Override
        protected void onPostExecute(Void v) {
            progressDialog.dismiss();
        }

        private String getRobotTeamNum(int robotID) {
            for (Robot r : robots) {
                if (r.getID() == robotID) {
                    return Integer.toString(r.getTeamNumber());
                }
            }
            return "";
        }
    }
}
