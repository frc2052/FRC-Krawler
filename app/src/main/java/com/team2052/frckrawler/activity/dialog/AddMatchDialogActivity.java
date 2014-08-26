package com.team2052.frckrawler.activity.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.MatchScheduleActivity;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Match;
import com.team2052.frckrawler.database.structures.Robot;

public class AddMatchDialogActivity extends Activity implements OnClickListener {
    private int eventID;
    private Robot[] robots;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_add_match);
        eventID = getIntent().getIntExtra(MatchScheduleActivity.EVENT_ID_EXTRA, -1);
        ((Button) findViewById(R.id.addNewMatch)).setOnClickListener(this);
        ((Button) findViewById(R.id.cancelAddMatch)).setOnClickListener(this);
        new PopulateSpinnersTask().execute();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.addNewMatch) {
            if (addMatch()) {
                setResult(RESULT_OK);
                finish();
            }
        } else if (v.getId() == R.id.cancelAddMatch) {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    private boolean addMatch() {
        int matchNum;
        int redScore = -1;
        int blueScore = -1;
        try {
            matchNum = Integer.parseInt(((EditText) findViewById(R.id.matchNumberEdit))
                    .getText().toString());
        } catch (NumberFormatException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Oops");
            builder.setMessage("Your match could not be added. You did not specify a " +
                    "match number.");
            builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface i, int which) {
                    i.dismiss();
                }
            });
            builder.show();
            return false;
        }
        try {
            redScore = Integer.parseInt(((EditText) findViewById(R.id.redScore))
                    .getText().toString());
        } catch (NumberFormatException e) {
        }
        try {
            blueScore = Integer.parseInt(((EditText) findViewById(R.id.blueScore))
                    .getText().toString());
        } catch (NumberFormatException e) {
        }
        int red1 = robots[((Spinner) findViewById(R.id.red1))
                .getSelectedItemPosition()].getID();
        int red2 = robots[((Spinner) findViewById(R.id.red2))
                .getSelectedItemPosition()].getID();
        int red3 = robots[((Spinner) findViewById(R.id.red3))
                .getSelectedItemPosition()].getID();
        int blue1 = robots[((Spinner) findViewById(R.id.blue1))
                .getSelectedItemPosition()].getID();
        int blue2 = robots[((Spinner) findViewById(R.id.blue2))
                .getSelectedItemPosition()].getID();
        int blue3 = robots[((Spinner) findViewById(R.id.blue3))
                .getSelectedItemPosition()].getID();
        return DBManager.getInstance(this).addMatch(eventID, new Match(
                matchNum,
                red1,
                red2,
                red3,
                blue1,
                blue2,
                blue3,
                redScore,
                blueScore));
    }


    /**
     * **
     * Class: PopulateSpinnersTask
     * <p/>
     * Summary: Populates the spinners that let the user
     * select what robots are on each alliance
     * ***
     */
    private class PopulateSpinnersTask extends AsyncTask<Void, Void, Robot[]> {

        @Override
        protected Robot[] doInBackground(Void... params) {
            return DBManager.getInstance(getApplicationContext())
                    .getRobotsAtEvent(eventID);
        }

        @Override
        protected void onPostExecute(Robot[] _robots) {
            robots = _robots;
            String[] teamNums = new String[robots.length];
            for (int i = 0; i < robots.length; i++)
                teamNums[i] = Integer.toString(robots[i].getTeamNumber());
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    AddMatchDialogActivity.this,
                    R.layout.scout_spinner_item,
                    teamNums);
            ((Spinner) findViewById(R.id.red1)).setAdapter(adapter);
            ((Spinner) findViewById(R.id.red2)).setAdapter(adapter);
            ((Spinner) findViewById(R.id.red3)).setAdapter(adapter);
            ((Spinner) findViewById(R.id.blue1)).setAdapter(adapter);
            ((Spinner) findViewById(R.id.blue2)).setAdapter(adapter);
            ((Spinner) findViewById(R.id.blue3)).setAdapter(adapter);
        }
    }
}
