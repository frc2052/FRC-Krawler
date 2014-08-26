package com.team2052.frckrawler.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.activity.dialog.ScoutScheduleDialogActivity;
import com.team2052.frckrawler.bluetooth.SyncAsScoutTask;
import com.team2052.frckrawler.bluetooth.SyncCallbackHandler;
import com.team2052.frckrawler.database.DBManager;
import com.team2052.frckrawler.database.structures.Event;
import com.team2052.frckrawler.database.structures.User;
import com.team2052.frckrawler.gui.ProgressSpinner;

public class ScoutTypeActivity extends RotationControlActivity implements OnClickListener,
        android.content.DialogInterface.OnClickListener,
        SyncCallbackHandler {

    private AlertDialog progressDialog;
    private Event selectedEvent;
    private DBManager db;
    private User user;
    private SyncAsScoutTask syncTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scout_type);
        findViewById(R.id.matchScout).setOnClickListener(this);
        findViewById(R.id.pitScout).setOnClickListener(this);
        findViewById(R.id.matchData).setOnClickListener(this);
        findViewById(R.id.matchSchedule).setOnClickListener(this);
        findViewById(R.id.sync).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        db = DBManager.getInstance(this);
        syncTask = null;
        User[] allUsers = db.scoutGetAllUsers();
        for (User u : allUsers)
            if (GlobalValues.userID == u.getID())
                user = u;
        ((TextView) findViewById(R.id.scoutName)).setText(user.getName());
        selectedEvent = db.scoutGetEvent();
        if (selectedEvent != null)
            ((TextView) findViewById(R.id.eventInfo)).setText
                    (selectedEvent.getEventName() + ", " + selectedEvent.getGameName());
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.matchScout:
                i = new Intent(this, ScoutActivity.class);
                i.putExtra(ScoutActivity.SCOUT_TYPE_EXTRA,
                        ScoutActivity.SCOUT_TYPE_MATCH);
                startActivity(i);
                break;

            case R.id.pitScout:
                i = new Intent(this, ScoutActivity.class);
                i.putExtra(ScoutActivity.SCOUT_TYPE_EXTRA,
                        ScoutActivity.SCOUT_TYPE_PIT);
                startActivity(i);
                break;

            case R.id.matchData:
                i = new Intent(this, ScoutMatchDataActivity.class);
                startActivity(i);
                break;

            case R.id.matchSchedule:
                i = new Intent(this, ScoutScheduleDialogActivity.class);
                startActivity(i);
                break;

            case R.id.sync:
                SharedPreferences prefs = getSharedPreferences
                        (GlobalValues.PREFS_FILE_NAME, 0);
                String macAdress = prefs.getString(GlobalValues.MAC_ADRESS_PREF, "null");
                if (macAdress.equals("null")) {
                    Toast.makeText(this, "Sync failed. No server " +
                            "adress remembered.", Toast.LENGTH_SHORT).show();
                    break;
                }
                syncTask = new SyncAsScoutTask(this, this);
                syncTask.execute(BluetoothAdapter.getDefaultAdapter()
                        .getRemoteDevice(macAdress));
                break;

            case R.id.logout:
                finish();
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            syncTask.cancel(true);
        }
    }

    @Override
    public void onSyncStart(String deviceName) {
        lockScreenOrientation();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Syncing...");
        builder.setView(new ProgressSpinner(this));
        builder.setNeutralButton("Cancel", this);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
        progressDialog.show();
    }

    @Override
    public void onSyncSuccess(String deviceName) {
        progressDialog.dismiss();
        releaseScreenOrientation();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sync Success");
        builder.setMessage("Sync with the server was successful.");
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @Override
    public void onSyncCancel(String deviceName) {
        progressDialog.dismiss();
        releaseScreenOrientation();
    }

    @Override
    public void onSyncError(String deviceName) {
        progressDialog.dismiss();
        releaseScreenOrientation();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sync Error");
        builder.setMessage("There was an error in syncing with the server. Make sure " +
                "that the server device is turned on and is running the FRCKrawler server.");
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
