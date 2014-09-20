package com.team2052.frckrawler.fragment;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.astuetz.PagerSlidingTabStrip;
import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.SyncAsScoutTask;
import com.team2052.frckrawler.bluetooth.SyncCallbackHandler;
import com.team2052.frckrawler.database.models.Event;
import com.team2052.frckrawler.database.models.User;
import com.team2052.frckrawler.gui.ProgressSpinner;

import java.util.List;

public class ScoutFragment extends Fragment implements DialogInterface.OnClickListener, SyncCallbackHandler {

    private ViewPager mPager;
    private PagerSlidingTabStrip mTabs;
    private BluetoothDevice[] devices;
    private EditText scoutLoginName;
    private SyncAsScoutTask scoutSyncTask;
    private AlertDialog progressDialog;
    private int selectedDeviceAddress;
    private int REQUEST_BT_ENABLE = 1;
    private Event mEvent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FOR DEBUG REASONS
        /*if (BluetoothAdapter.getDefaultAdapter() != null) {
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
        }*/
        mEvent = new Select().from(Event.class).executeSingle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scout, null);
        mPager = (ViewPager) view.findViewById(R.id.pager);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mPager.setAdapter(new ScoutPagerAdapter(getFragmentManager()));
        mTabs.setViewPager(mPager);
        return view;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
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

    private void startScoutSync() {
        SharedPreferences prefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        scoutSyncTask = new SyncAsScoutTask(getActivity(), this);
        scoutSyncTask.execute(devices[selectedDeviceAddress]);
        prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, devices[selectedDeviceAddress].getAddress());
        prefsEditor.commit();
    }

    @Override
    public void onSyncCancel(String deviceName) {
        progressDialog.dismiss();
    }

    @Override
    public void onSyncError(String deviceName) {
        progressDialog.dismiss();
        //releaseScreenOrientation();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    @Override
    public void onSyncStart(String deviceName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Syncing...");
        builder.setView(new ProgressSpinner(getActivity()));
        builder.setNeutralButton("Cancel", this);
        builder.setCancelable(false);
        progressDialog = builder.create();
        progressDialog.show();
    }

    @Override
    public void onSyncSuccess(String deviceName) {
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

    public class ScoutPagerAdapter extends FragmentPagerAdapter {
        public final String[] headers = {"Home", "Match Scouting", "Pit Scouting", "Summary", "Schedule"};

        public ScoutPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return headers[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new ScoutHomeFragment();
                    break;
                case 1:
                    fragment = new ScoutMatchFragment();
                    break;
                case 2:
                    fragment = new ScoutPitFragment();
                    break;
                case 3:
                    fragment = new ScoutHomeFragment();
                    break;
                case 4:
                    fragment = MatchListFragment.newInstance(mEvent);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return headers.length;
        }
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
