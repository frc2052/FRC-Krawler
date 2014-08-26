package com.team2052.frckrawler.activity.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.database.DBContract;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.User;

public class EditUserDialogActivity extends Activity implements OnClickListener, DialogInterface.OnClickListener {

    public static final String USER_ID_EXTRA = "com.team2052.frckrawler.userID";

    private DBManager dbManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialogactivity_edit_user);

        ((Button) findViewById(R.id.saveUser)).setOnClickListener(this);
        ((Button) findViewById(R.id.remove)).setOnClickListener(this);
        ((Button) findViewById(R.id.cancel)).setOnClickListener(this);

        dbManager = DBManager.getInstance(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        User[] arr = dbManager.getUsersByColumns(new String[]{DBContract.COL_USER_ID},new String[]{getIntent().getStringExtra(USER_ID_EXTRA)});
        User u = arr[0];
        ((TextView) findViewById(R.id.nameVal)).setText(u.getName());
    }


    /**
     * **
     * Method: onClick
     * <p/>
     * Summary: This method is the callback method for the Views that
     * belong to this Activity.
     * ***
     */

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel:
                finish();
                break;

            case R.id.saveUser:
                String[] queryCols = new String[]{
                        DBContract.COL_USER_ID
                };

                String[] queryVals = new String[]{
                        getIntent().getStringExtra(USER_ID_EXTRA)
                };

                String[] updateCols = new String[]{
                        DBContract.COL_USER_NAME
                };

                String[] updateVals = new String[]{
                        ((TextView) findViewById(R.id.nameVal)).getText().toString(),
                };

                dbManager.updateUsers(queryCols, queryVals, updateCols, updateVals);
                finish();
                break;

            case R.id.remove:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to remove this user from the database? " +
                        "They will be cast into the cold void of cyberspace for eternity.");
                builder.setTitle("");
                builder.setPositiveButton("Yes", this);
                builder.setNegativeButton("No", this);
                builder.show();
                break;
        }
    }


    /**
     * **
     * Method: onClick
     *
     * @param dialog
     * @param which  Summary: This method is the callback for the AlertDialog
     *               that pops up when the user wants to delete a user. It
     *               does nothing, or removes the user, depending on what
     *               the user wants.
     *               ***
     */

    @Override
    public void onClick(DialogInterface dialog, int which) {

        if (which == DialogInterface.BUTTON_POSITIVE) {
            dbManager.removeUser(Integer.parseInt(getIntent().getStringExtra(USER_ID_EXTRA)));
            dialog.dismiss();
            finish();

        } else {
            dialog.dismiss();
        }

    }
}
