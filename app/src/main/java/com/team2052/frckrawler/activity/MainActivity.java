package com.team2052.frckrawler.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.SyncAsScoutTask;
import com.team2052.frckrawler.bluetooth.SyncCallbackHandler;
import com.team2052.frckrawler.database.models.User;
import com.team2052.frckrawler.fragment.dialog.AboutDialogFragment;
import com.team2052.frckrawler.gui.ProgressSpinner;

import java.util.List;

public class MainActivity extends BaseActivity implements DialogInterface.OnClickListener, OnClickListener, SyncCallbackHandler {

    private static final int REQUEST_BT_ENABLE = 1;

    private int selectedDeviceAddress;
    private AlertDialog progressDialog;
    private EditText scoutLoginName;
    private SyncAsScoutTask scoutSyncTask;
    private BluetoothDevice[] devices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.admininster).setOnClickListener(this);
        findViewById(R.id.join).setOnClickListener(this);
        findViewById(R.id.continueScouting).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.admininster:
                break;
            case R.id.join:
                openJoin();
                break;
            case R.id.continueScouting:
                continueScouting();
                break;
        }
    }

    private void continueScouting() {
        AlertDialog.Builder builder;
        if (new Select().from(User.class).execute().size() == 0) {
            Toast.makeText(this, "This device has not been synced with a database. " + "Hit the 'Join' button to sync.", Toast.LENGTH_LONG).show();
            return;
        }

        scoutLoginName = new EditText(MainActivity.this);
        scoutLoginName.setHint("Name");
        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Login");
        builder.setView(scoutLoginName);
        builder.setPositiveButton("Login", new UserDialogListener());
        builder.setNegativeButton("Cancel", new UserDialogListener());
        builder.show();
    }

    private void openJoin() {
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            AlertDialog.Builder builder;
            java.util.Set<BluetoothDevice> var = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
            devices = var.toArray(new BluetoothDevice[var.size()]);
            CharSequence[] deviceNames = new String[devices.length];
            for (int k = 0; k < deviceNames.length; k++)
                deviceNames[k] = devices[k].getName();
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Server Device");
            builder.setItems(deviceNames, this);
            builder.show();
        } else {
            Toast.makeText(this, "Sorry, your device does not support Bluetooth. " + "You are unable to sync with a server.", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            scoutSyncTask.cancel(true);
        } else {
            selectedDeviceAddress = which;
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                Toast.makeText(this, "Sorry, your device does not support " + "Bluetooth. You may not sync with another database.", Toast.LENGTH_LONG).show();
                return;
            }
            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            } else
                startScoutSync();
        }
    }


    public void onCreditsClicked(View v) {
        /*Intent i = new Intent(this, AboutDialogActivity.class);
        startActivity(i);*/
        AboutDialogFragment aboutDialogFragment = new AboutDialogFragment();
        aboutDialogFragment.show(getFragmentManager(), "About");
    }

    private void startScoutSync() {
        SharedPreferences prefs = getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        Editor prefsEditor = prefs.edit();
        scoutSyncTask = new SyncAsScoutTask(this, this);
        scoutSyncTask.execute(devices[selectedDeviceAddress]);
        prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, devices[selectedDeviceAddress].getAddress());
        prefsEditor.commit();
    }

    @Override
    public void onSyncStart(String deviceName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Syncing...");
        builder.setView(new ProgressSpinner(this));
        builder.setNeutralButton("Cancel", this);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
        //lockScreenOrientation();
    }

    @Override
    public void onSyncSuccess(String deviceName) {
        progressDialog.dismiss();
        scoutLoginName = new EditText(MainActivity.this);
        scoutLoginName.setHint("Name");
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Login");
        builder.setView(scoutLoginName);
        builder.setPositiveButton("Login", new UserDialogListener());
        builder.setNegativeButton("Cancel", new UserDialogListener());
        builder.show();
        //releaseScreenOrientation();
    }

    @Override
    public void onSyncCancel(String deviceName) {
        progressDialog.dismiss();
        //releaseScreenOrientation();
    }

    @Override
    public void onSyncError(String deviceName) {
        progressDialog.dismiss();
        //releaseScreenOrientation();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sync Error");
        builder.setMessage("There was an error in syncing with the server. Make sure " + "that the server device is turned on and is running the FRCKrawler server.");
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private class UserDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                List<User> users = new Select().from(User.class).execute();
                boolean isValid = false;
                for (User u : users) {
                    if (u.name.equals(scoutLoginName.getText().toString())) {
                        GlobalValues.userID = u.getId();
                        isValid = true;
                    }
                }
                if (isValid) {
                    /*Intent i = new Intent(getApplicationContext(), ScoutTypeActivity.class);
                    startActivity(i);*/
                } else {
                    Toast.makeText(getApplicationContext(), "Not a valid username. " + "The username must already be in the database.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    scoutLoginName = new EditText(MainActivity.this);
                    scoutLoginName.setHint("Name");
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Login");
                    builder.setView(scoutLoginName);
                    builder.setPositiveButton("Login", new UserDialogListener());
                    builder.setNegativeButton("Cancel", new UserDialogListener());
                    builder.show();
                }
            } else {
                dialog.dismiss();
            }
        }
    }
}