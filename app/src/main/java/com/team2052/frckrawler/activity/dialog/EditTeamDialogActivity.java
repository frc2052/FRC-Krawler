package com.team2052.frckrawler.activity.dialog;

/*****
 * Class: EditTeamDialogActivity
 *
 *
 *****/

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.BaseActivity;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Team;

public class EditTeamDialogActivity extends BaseActivity implements OnClickListener, DialogInterface.OnClickListener {

    public static final String TEAM_NUMBER_EXTRA_KEY = "com.team2052.frckrawler.editTeamNumber";

    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode
                (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.dialogactivity_edit_team);

        ((Button) findViewById(R.id.saveButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.remove)).setOnClickListener(this);
        ((Button) findViewById(R.id.cancel)).setOnClickListener(this);

        dbManager = DBManager.getInstance(this);
    }

    @Override
    public void onResume() {

        super.onResume();

        Team t;
        Team[] arr = (dbManager.getTeamsByColumns(new String[]{DBContract.COL_TEAM_NUMBER},
                new String[]{Integer.toString(getIntent().getIntExtra(TEAM_NUMBER_EXTRA_KEY, -1))}));

        if (arr.length > 0)
            t = arr[0];
        else
            return;

        Spinner stateSpinner = (Spinner) findViewById(R.id.stateVal);
        int stateSelection = ((ArrayAdapter<String>) stateSpinner.getAdapter()).
                getPosition(t.getStatePostalCode());

        String rookieYearString = new String();

        if (t.getRookieYear() != -1 && t.getRookieYear() != 0)
            rookieYearString = Integer.toString(t.getRookieYear());

        ((TextView) findViewById(R.id.numberVal)).setText(Integer.toString(t.getNumber()));
        ((EditText) findViewById(R.id.nameVal)).setText(t.getName());
        ((EditText) findViewById(R.id.schoolVal)).setText(t.getSchool());
        ((EditText) findViewById(R.id.cityVal)).setText(t.getCity());
        ((EditText) findViewById(R.id.rookieYearVal)).setText(rookieYearString);
        ((EditText) findViewById(R.id.websiteVal)).setText(t.getWebsite());
        stateSpinner.setSelection(stateSelection);
        ((EditText) findViewById(R.id.colorsVal)).setText(t.getColors());
    }


    /**
     * **
     * Method: onClick
     * <p/>
     * Summary: Called when the user presses a button. The View
     * is the button that the user pressed.
     * ***
     */

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.saveButton) {

            String rookieYear = ((TextView) findViewById(R.id.rookieYearVal)).getText().toString();

            if (rookieYear.equals("0") || rookieYear.equals("-1"))
                rookieYear = "";

            String[] queryVals = new String[]{((TextView) findViewById(R.id.numberVal)).
                    getText().toString()};
            String[] updateCols = new String[]{
                    DBContract.COL_TEAM_NAME,
                    DBContract.COL_SCHOOL,
                    DBContract.COL_CITY,
                    DBContract.COL_ROOKIE_YEAR,
                    DBContract.COL_WEBSITE,
                    DBContract.COL_STATE_POSTAL_CODE,
                    DBContract.COL_COLORS
            };

            String[] updateVals = new String[]{
                    ((TextView) findViewById(R.id.nameVal)).getText().toString(),
                    ((TextView) findViewById(R.id.schoolVal)).getText().toString(),
                    ((TextView) findViewById(R.id.cityVal)).getText().toString(),
                    rookieYear,
                    ((TextView) findViewById(R.id.websiteVal)).getText().toString(),
                    ((Spinner) findViewById(R.id.stateVal)).getSelectedItem().toString(),
                    ((TextView) findViewById(R.id.colorsVal)).getText().toString()
            };
            dbManager.updateTeams(new String[]{DBContract.COL_TEAM_NUMBER},
                    queryVals, updateCols, updateVals);
            setResult(RESULT_OK);
            finish();
        } else if (v.getId() == R.id.cancel) {
            finish();
        } else if (v.getId() == R.id.remove) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to remove this team and all its data?");
            builder.setPositiveButton("Yes", this);
            builder.setNegativeButton("No", this);
            builder.show();
        }
    }


    /**
     * **
     * Method: onClick
     *
     * @param dialog
     * @param which  Summary: This method is the callback for the AlertDialog
     *               that pops up when the user wants to delete a team. It
     *               does nothing, or removes the team, depending on what
     *               the user wants.
     *               ***
     */

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == DialogInterface.BUTTON_POSITIVE) {

            dbManager.removeTeam(getIntent().getIntExtra(TEAM_NUMBER_EXTRA_KEY, -1));
            dialog.dismiss();
            setResult(RESULT_OK);
            finish();

        } else {
            dialog.dismiss();
        }

    }
}
