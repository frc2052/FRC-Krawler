package com.team2052.frckrawler.fragment;

import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;

import com.activeandroid.query.Select;
import com.team2052.frckrawler.*;
import com.team2052.frckrawler.bluetooth.*;
import com.team2052.frckrawler.database.models.User;
import com.team2052.frckrawler.gui.ProgressSpinner;

import java.util.List;

/**
 * @author Adam
 */
public class ScoutHomeFragment extends Fragment implements View.OnClickListener, SyncCallbackHandler, AlertDialog.OnClickListener
{
    private SyncAsScoutTask scoutSyncTask;
    private Dialog progressDialog;
    private EditText scoutLoginName;
    private BluetoothDevice[] devices;
    private int selectedDeviceAddress;
    private int REQUEST_BT_ENABLE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.activity_scout_type, null);
        view.findViewById(R.id.sync).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.sync:
                if (BluetoothAdapter.getDefaultAdapter() != null) {
                    AlertDialog.Builder builder;
                    devices = BluetoothAdapter.getDefaultAdapter().getBondedDevices().toArray(new BluetoothDevice[0]);
                    CharSequence[] deviceNames = new String[devices.length];
                    for (int k = 0; k < deviceNames.length; k++)
                        deviceNames[k] = devices[k].getName();
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Server Device");
                    builder.setItems(deviceNames, this);
                    builder.show();
                } else {
                    Toast.makeText(getActivity(), "Sorry, your device does not support Bluetooth. " + "You are unable to sync with a server.", Toast.LENGTH_LONG);
                }
                break;
            case R.id.login:
                break;
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
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);

        //Used to tell deny access to edit any of the database besides putting match data.
        SharedPreferences.Editor editor = scoutPrefs.edit();
        editor.putBoolean(GlobalValues.IS_SCOUT_PREF, true);
        editor.apply();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Login");
        builder.setView(scoutLoginName);
        builder.setPositiveButton("Login", new UserDialogListener());
        builder.setNegativeButton("Cancel", new UserDialogListener());
        builder.show();
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
