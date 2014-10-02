package com.team2052.frckrawler.fragment.dialog;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.bluetooth.SyncAsScoutTask;
import com.team2052.frckrawler.bluetooth.SyncCallbackHandler;
import com.team2052.frckrawler.database.models.User;
import com.team2052.frckrawler.gui.ProgressSpinner;

import java.util.List;

/**
 * @author Adam
 */
public class ScoutJoinDialogFragment extends DialogFragment implements DialogInterface.OnClickListener, SyncCallbackHandler
{
    private static final int REQUEST_BT_ENABLE = 1;
    private BluetoothDevice[] devices;
    private int selectedDeviceAddress;
    private AlertDialog progressDialog;
    private SyncAsScoutTask scoutSyncTask;
    private EditText scoutLoginName;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onClick(DialogInterface dialog, int which)
    {
        if (which == DialogInterface.BUTTON_NEUTRAL) {
            scoutSyncTask.cancel(true);
        } else {
            selectedDeviceAddress = which;
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                Toast.makeText(getActivity(), "Sorry, your device does not support " + "Bluetooth. You may not sync with another database.", Toast.LENGTH_LONG).show();
                return;
            }
            if (!adapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_BT_ENABLE);
            } else
                startScoutSync();
        }
    }

    private void startScoutSync()
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        scoutSyncTask = new SyncAsScoutTask(getActivity(), this);
        scoutSyncTask.execute(devices[selectedDeviceAddress]);
        prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, devices[selectedDeviceAddress].getAddress());
        prefsEditor.commit();
    }

    @Override
    public void onSyncStart(String deviceName)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Syncing...");
        builder.setView(new ProgressSpinner(getActivity()));
        builder.setNeutralButton("Cancel", this);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }

    @Override
    public void onSyncSuccess(String deviceName)
    {
        progressDialog.dismiss();
        scoutLoginName = new EditText(getActivity());
        scoutLoginName.setHint("Name");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Login");
        builder.setView(scoutLoginName);
        builder.setPositiveButton("Login", new UserDialogListener());
        builder.setNegativeButton("Cancel", new UserDialogListener());
        builder.show();
    }

    @Override
    public void onSyncCancel(String deviceName)
    {
        progressDialog.dismiss();
    }

    @Override
    public void onSyncError(String deviceName)
    {
        progressDialog.dismiss();
        //releaseScreenOrientation();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Sync Error");
        builder.setMessage("There was an error in syncing with the server. Make sure " + "that the server device is turned on and is running the FRCKrawler server.");
        builder.setNeutralButton("Close", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private class UserDialogListener implements DialogInterface.OnClickListener
    {
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
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
                    Toast.makeText(getActivity(), "Not a valid username. " + "The username must already be in the database.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    scoutLoginName = new EditText(getActivity());
                    scoutLoginName.setHint("Name");
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
