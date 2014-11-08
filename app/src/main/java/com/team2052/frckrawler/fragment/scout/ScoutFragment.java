package com.team2052.frckrawler.fragment.scout;

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
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.SyncAsScoutTask;
import com.team2052.frckrawler.bluetooth.SyncCallbackHandler;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.fragment.ViewPagerFragment;
import com.team2052.frckrawler.fragment.event.MatchListFragment;

import java.util.Set;

public class ScoutFragment extends ViewPagerFragment implements SyncCallbackHandler, DialogInterface.OnClickListener
{

    private Event mEvent;
    private boolean mNeedsSync;
    private SyncAsScoutTask scoutSyncTask;
    private BluetoothDevice[] devices;
    private int selectedDeviceAddress;
    private String mAddress;
    private int REQUEST_BT_ENABLE = 1;
    private Menu mOptionsMenu;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        mAddress = scoutPrefs.getString(GlobalValues.MAC_ADRESS_PREF, "null");
        if (scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
            mEvent = mDaoSession.getEventDao().load(scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
            mNeedsSync = false;
        } else {
            mNeedsSync = true;
        }
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_sync:

                if (!mAddress.contains("null")) {
                    scoutSyncTask = new SyncAsScoutTask(getActivity(), this);
                    scoutSyncTask.execute(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mAddress));
                } else if (BluetoothAdapter.getDefaultAdapter() != null) {
                    AlertDialog.Builder builder;
                    Set<BluetoothDevice> bondedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
                    devices = bondedDevices.toArray(new BluetoothDevice[bondedDevices.size()]);
                    CharSequence[] deviceNames = new String[devices.length];
                    for (int k = 0; k < deviceNames.length; k++)
                        deviceNames[k] = devices[k].getName();
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Server Device");
                    builder.setItems(deviceNames, this);
                    builder.show();
                } else {
                    Toast.makeText(getActivity(), "Sorry, your device does not support Bluetooth. " + "You are unable to sync with a server.", Toast.LENGTH_LONG).show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.scout_main_sync, menu);
        mOptionsMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public PagerAdapter setAdapter()
    {
        return new ScoutPagerAdapter(getChildFragmentManager());
    }

    private void startScoutSync()
    {
        SharedPreferences prefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        scoutSyncTask = new SyncAsScoutTask(getActivity(), this);
        scoutSyncTask.execute(devices[selectedDeviceAddress]);
        prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, devices[selectedDeviceAddress].getAddress());
        prefsEditor.apply();
    }

    @Override
    public void onSyncCancel(String deviceName)
    {
        mOptionsMenu.findItem(R.id.menu_sync).setActionView(null);
    }

    @Override
    public void onSyncError(String deviceName)
    {
        mOptionsMenu.findItem(R.id.menu_sync).setActionView(null);
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
        mOptionsMenu.findItem(R.id.menu_sync).setActionView(R.layout.actionbar_progress);
    }

    @Override
    public void onSyncSuccess(String deviceName)
    {
        mOptionsMenu.findItem(R.id.menu_sync).setActionView(null);
        /*scoutLoginName = new EditText(getActivity());
        scoutLoginName.setHint("Name");*/

        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        //Used to tell deny access to edit any of the database besides putting match data.
        SharedPreferences.Editor editor = scoutPrefs.edit();
        editor.putBoolean(GlobalValues.IS_SCOUT_PREF, true);
        editor.apply();

        //TODO LOGIN
        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Login");
        builder.setView(scoutLoginName);
        builder.setPositiveButton("Login", new UserDialogListener());
        builder.setNegativeButton("Cancel", new UserDialogListener());
        builder.show();*/
        reload();
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which)
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

    private void reload()
    {
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        mAddress = scoutPrefs.getString(GlobalValues.MAC_ADRESS_PREF, "null");
        if (scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
            mEvent = mDaoSession.getEventDao().load(scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
            mNeedsSync = false;
        } else {
            mNeedsSync = true;
        }
        mViewPager.setAdapter(setAdapter());
    }


    public class ScoutPagerAdapter extends FragmentPagerAdapter
    {
        public final String[] headers = {"Match Scouting", "Pit Scouting", "Schedule"};

        public ScoutPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return headers[position];
        }

        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    if (!mNeedsSync) {
                        fragment = ScoutMatchFragment.newInstance(mEvent);
                    } else {
                        fragment = new NeedSyncFragment();
                    }
                    break;
                case 1:
                    if (!mNeedsSync) {
                        fragment = ScoutPitFragment.newInstance(mEvent);
                    } else {
                        fragment = new NeedSyncFragment();
                    }
                    break;
                case 2:
                    if (!mNeedsSync) {
                        fragment = MatchListFragment.newInstance(mEvent);
                    } else {
                        fragment = new NeedSyncFragment();
                    }
            }
            return fragment;
        }

        @Override
        public int getCount()
        {
            return headers.length;
        }
    }
}
