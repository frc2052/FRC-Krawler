package com.team2052.frckrawler.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.DeleteAllDataTask;
import com.team2052.frckrawler.bluetooth.client.ScoutSyncHandler;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.bluetooth.server.events.ServerStateChangeEvent;
import com.team2052.frckrawler.bluetooth.server.events.ServerStateRequestEvent;
import com.team2052.frckrawler.databinding.ActivityScoutMainBinding;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.listitems.items.NavDrawerItem;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.ScoutUtil;

import de.greenrobot.event.EventBus;

/**
 * @author adam
 * @since 12/27/14.
 */
public class ScoutMainActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_ENABLE_BT = 0;
    public ScoutSyncHandler mSyncHandler;
    Event currentEvent;
    private ActivityScoutMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mSyncHandler = ScoutSyncHandler.getInstance(this);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_scout_main);
        useActionBarToggle();

        binding.scoutMatchButton.setOnClickListener(this);
        binding.scoutPitButton.setOnClickListener(this);
        binding.scoutPracticeButton.setOnClickListener(this);

        //Binding doesn't include <include> layout
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle("Scout");
        }

        EventBus.getDefault().post(new ServerStateRequestEvent());
    }

    @Override
    public void onClick(View v) {
        if (isCurrentEventValid()) {
            switch (v.getId()) {
                case R.id.scout_match_button:
                    startActivity(ScoutActivity.newInstance(this, currentEvent, ScoutActivity.MATCH_SCOUT_TYPE));
                    break;
                case R.id.scout_pit_button:
                    startActivity(ScoutActivity.newInstance(this, currentEvent, ScoutActivity.PIT_SCOUT_TYPE));
                    break;
                case R.id.scout_practice_button:
                    startActivity(ScoutActivity.newInstance(this, currentEvent, ScoutActivity.PRACTICE_MATCH_SCOUT_TYPE));
                    break;
            }
        } else {
            Snackbar.make(binding.container, "Cannot open, you must have a synced event", Snackbar.LENGTH_SHORT).show();
        }

        if (v.getId() == R.id.sync_button) {
            if (BluetoothUtil.hasBluetoothAdapter()) {
                if (!BluetoothUtil.isBluetoothEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                } else {
                    mSyncHandler.startScoutSync(this);
                }
            }
        }
    }

    public void onEvent(ServerStateChangeEvent event) {
        if (event.getEvent() != null && event.getState()) {
            binding.scoutSyncContainer.setVisibility(View.GONE);
            setCurrentEvent(event.getEvent());
        } else {
            binding.scoutSyncContainer.setVisibility(View.VISIBLE);
            setCurrentEvent(ScoutUtil.getScoutEvent(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scout_main_sync, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //TODO: Only allow on scout
            case R.id.reset_server_device:
                ScoutUtil.resetSyncDevice(this);
                new DeleteAllDataTask(mDbManager).execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        int id = item.getId();
        if (id != R.id.nav_item_scout) {
            finish();
            startActivity(HomeActivity.newInstance(this, id));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setNavigationDrawerItemSelected(R.id.nav_item_scout);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event) {
        setProgress(View.GONE);
        Snackbar.make(findViewById(R.id.container), "Sync Successful", Snackbar.LENGTH_LONG).show();
        setCurrentEvent(ScoutUtil.getScoutEvent(this));
    }

    public void setCurrentEvent(Event event) {
        currentEvent = event;
        if (isCurrentEventValid()) {
            getSupportActionBar().setSubtitle(currentEvent.getName());
        }
    }

    private void setProgressVisibility(int view_state) {
        binding.syncButton.setVisibility(view_state == View.VISIBLE ? View.GONE : View.VISIBLE);
        binding.syncProgressBar.setVisibility(view_state);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            mSyncHandler.startScoutSync(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncCancelledEvent event) {
        setProgress(View.GONE);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncErrorEvent event) {
        setProgress(View.GONE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getBaseContext().getString(R.string.sync_error_title));
        builder.setMessage(getBaseContext().getString(R.string.sync_error_message));
        builder.setNeutralButton(getString(R.string.close), (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncStartEvent event) {
        Snackbar.make(findViewById(R.id.container), "Starting Sync", Snackbar.LENGTH_SHORT).show();
        setProgress(View.VISIBLE);
    }


    public boolean isCurrentEventValid() {
        return currentEvent != null;
    }

}
