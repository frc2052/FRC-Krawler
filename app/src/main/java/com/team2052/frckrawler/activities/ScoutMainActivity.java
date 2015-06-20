package com.team2052.frckrawler.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.background.DeleteAllDataTask;
import com.team2052.frckrawler.bluetooth.client.ScoutSyncHandler;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.bluetooth.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.bluetooth.server.events.ServerStateChangeEvent;
import com.team2052.frckrawler.bluetooth.server.events.ServerStateRequestEvent;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.listitems.items.NavDrawerItem;
import com.team2052.frckrawler.util.BluetoothUtil;
import com.team2052.frckrawler.util.ScoutUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * @author adam
 * @since 12/27/14.
 */
public class ScoutMainActivity extends BaseActivity {

    private static final int REQUEST_ENABLE_BT = 0;
    public ScoutSyncHandler mSyncHandler;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.sync_button)
    ImageButton sync_button;
    @InjectView(R.id.sync_progress_bar)
    ProgressBar sync_progress_bar;
    @InjectView(R.id.scout_sync_container)
    RelativeLayout scoutSyncContainer;
    @Nullable
    Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        useActionBarToggle();
        mSyncHandler = ScoutSyncHandler.getInstance(this);
        setContentView(R.layout.activity_scout);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setTitle("Scout");
        }

        currentEvent = ScoutUtil.getScoutEvent(this, mDbManager);
        if (currentEvent != null) {
            getSupportActionBar().setSubtitle(currentEvent.getName());
        }

        EventBus.getDefault().post(new ServerStateRequestEvent());
    }


    public void onEvent(ServerStateChangeEvent event) {
        Log.d("ScoutMainActivity", "onEvent ServerStateChangeEvent");
        if (event.getEvent() != null || event.getState()) {
            scoutSyncContainer.setVisibility(View.GONE);
            setCurrentEvent(event.getEvent());
        }
    }

    @OnClick({R.id.scout_match_button, R.id.scout_practice_button, R.id.scout_pit_button})
    public void onScoutTypePressed(View view) {
        if (currentEvent != null) {
            switch (view.getId()) {
                case R.id.scout_pit_button:
                    startActivity(ScoutActivity.newInstance(this, currentEvent, ScoutActivity.PIT_SCOUT_TYPE));
                    break;
                case R.id.scout_practice_button:
                    startActivity(ScoutActivity.newInstance(this, currentEvent, ScoutActivity.PRACTICE_MATCH_SCOUT_TYPE));
                    break;
                case R.id.scout_match_button:
                    startActivity(ScoutActivity.newInstance(this, currentEvent, ScoutActivity.MATCH_SCOUT_TYPE));
                    break;
            }
        } else {
            Snackbar.make(findViewById(R.id.container), "You have to sync the device. No Synced Event.", Snackbar.LENGTH_SHORT).show();
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

    @OnClick(R.id.sync_button)
    public void handleSyncButton(View sync_button) {
        if (BluetoothUtil.hasBluetoothAdapter()) {
            if (!BluetoothUtil.isBluetoothEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                mSyncHandler.startScoutSync(this);
            }
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event) {
        setProgress(false);
        Snackbar.make(findViewById(R.id.container), "Sync Successful", Snackbar.LENGTH_LONG).show();
        setCurrentEvent(ScoutUtil.getScoutEvent(this, mDbManager));
    }

    public void setCurrentEvent(Event event) {
        currentEvent = event;
        if (currentEvent != null) {
            getSupportActionBar().setSubtitle(currentEvent.getName());
        }
    }

    private void setProgress(boolean toggle) {
        if (toggle) {
            sync_button.setVisibility(View.GONE);
            sync_progress_bar.setVisibility(View.VISIBLE);
        } else {
            sync_button.setVisibility(View.VISIBLE);
            sync_progress_bar.setVisibility(View.GONE);
        }
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
        setProgress(false);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncErrorEvent event) {
        setProgress(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getBaseContext().getString(R.string.sync_error_title));
        builder.setMessage(getBaseContext().getString(R.string.sync_error_message));
        builder.setNeutralButton(getString(R.string.close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncStartEvent event) {
        Snackbar.make(findViewById(R.id.container), "Starting Sync", Snackbar.LENGTH_SHORT).show();
        setProgress(true);
    }

}
