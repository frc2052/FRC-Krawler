package com.team2052.frckrawler.fragment.scout;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
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
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.events.scout.NotifyScoutEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncErrorEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncStartEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.fragment.ViewPagerFragment;
import com.team2052.frckrawler.util.BluetoothUtil;

import de.greenrobot.event.EventBus;

public class ScoutFragment extends ViewPagerFragment implements DialogInterface.OnClickListener
{

    public static final int REQUEST_BT_ENABLE = 1;
    private Event mEvent;
    private boolean mNeedsSync;
    private SyncAsScoutTask scoutSyncTask;
    private int selectedDevice;
    private String mAddress;
    private Menu mOptionsMenu;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        mAddress = scoutPrefs.getString(GlobalValues.MAC_ADRESS_PREF, "null");
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                if (!mAddress.contains("null")) {
                    scoutSyncTask = new SyncAsScoutTask(getActivity());
                    scoutSyncTask.execute(BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mAddress));
                } else if (BluetoothUtil.hasBluetoothAdapter()) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Server Device");
                    builder.setItems(ScoutUtil.getDeviceNames(ScoutUtil.getAllBluetoothDevices()), this);
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
        scoutSyncTask = new SyncAsScoutTask(getActivity());
        scoutSyncTask.execute(ScoutUtil.getAllBluetoothDevicesArray()[selectedDevice]);
        prefsEditor.putString(GlobalValues.MAC_ADRESS_PREF, ScoutUtil.getAllBluetoothDevicesArray()[selectedDevice].getAddress());
        prefsEditor.apply();
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncCancelledEvent event)
    {
        mOptionsMenu.findItem(R.id.menu_sync).setActionView(null);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncErrorEvent event)
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

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncStartEvent event)
    {
        mOptionsMenu.findItem(R.id.menu_sync).setActionView(R.layout.actionbar_progress);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event)
    {
        Toast.makeText(getActivity(), "Sync Successful", Toast.LENGTH_LONG).show();
        mOptionsMenu.findItem(R.id.menu_sync).setActionView(null);
        /*scoutLoginName = new EditText(getActivity());
        scoutLoginName.setHint("Name");*/
        ScoutUtil.setDeviceAsScout(getActivity(), true);
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
            selectedDevice = which;
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

    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void reload()
    {
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        mAddress = scoutPrefs.getString(GlobalValues.MAC_ADRESS_PREF, "null");
        if (scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE) != Long.MIN_VALUE) {
            mEvent = mDaoSession.getEventDao().load(scoutPrefs.getLong(GlobalValues.CURRENT_SCOUT_EVENT_ID, Long.MIN_VALUE));
        }
        EventBus.getDefault().post(new NotifyScoutEvent(mEvent));
    }

    public class ScoutPagerAdapter extends FragmentPagerAdapter
    {
        public final String[] headers = {"Match Scouting", "Pit Scouting"};

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
                    fragment = new ScoutMatchFragment();
                    break;
                case 1:
                    fragment = new ScoutPitFragment();
                    break;
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
