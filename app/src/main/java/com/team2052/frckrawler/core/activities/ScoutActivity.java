package com.team2052.frckrawler.core.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.client.LoginHandler;
import com.team2052.frckrawler.client.ScoutSyncHandler;
import com.team2052.frckrawler.client.background.DeleteAllDataTask;
import com.team2052.frckrawler.client.events.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.client.events.ScoutSyncErrorEvent;
import com.team2052.frckrawler.client.events.ScoutSyncStartEvent;
import com.team2052.frckrawler.client.events.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.core.adapters.ScoutPagerAdapter;
import com.team2052.frckrawler.core.listitems.items.NavDrawerItem;
import com.team2052.frckrawler.core.util.Utilities;
import com.team2052.frckrawler.db.Event;
import com.team2052.frckrawler.server.Server;

import de.greenrobot.event.EventBus;

/**
 * @author adam
 * @since 12/27/14.
 */
//TODO save current state of the fragments -- Ask Bryan
public class ScoutActivity extends ViewPagerActivity {

    private final int REQUEST_ENABLE_BT = 0;
    private ScoutSyncHandler mSyncHandler;
    private MenuItem loadingMenuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSyncHandler = ScoutSyncHandler.getInstance(this);
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Scout");
        Event event = Utilities.ScoutUtil.getScoutEvent(this, mDbManager);
        if (event != null) {
            getSupportActionBar().setSubtitle(event.getName());
        }
    }

    @Override
    public PagerAdapter setAdapter() {
        return new ScoutPagerAdapter(getSupportFragmentManager(), Utilities.ScoutUtil.getScoutEvent(this, mDbManager));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scout_main_sync, menu);
        loadingMenuIcon = menu.findItem(R.id.menu_sync);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                handleSyncButton();
                break;
            case R.id.reset_server_device:
                Utilities.ScoutUtil.resetSyncDevice(this);
                new DeleteAllDataTask(mDbManager).execute();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncCancelledEvent event) {
        setProgress(false);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncErrorEvent event) {
        setProgress(false);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncStartEvent event) {
        setProgress(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setNavigationDrawerItemSelected(R.id.nav_item_scout);
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        int id = item.getId();
        if (id != R.id.nav_item_scout) {
            startActivity(HomeActivity.newInstance(this, id));
        }
    }

    private void handleSyncButton() {
        if (Server.getInstance(this).isOpen()) {
            Toast.makeText(this, "You cannot sync with a server if you are running a server", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Utilities.BluetoothUtil.hasBluetoothAdapter()) {
            if (!Utilities.BluetoothUtil.isBluetoothEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                mSyncHandler.startScoutSync();
            }
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event) {
        setProgress(false);
        LoginHandler loginHandler = LoginHandler.getInstance(this, mDbManager);
        if (!loginHandler.isLoggedOn()) {
            loginHandler.login(this);
        } else if (!loginHandler.loggedOnUserStillExists()) {
            loginHandler.login(this);
        }
        Event scoutEvent = Utilities.ScoutUtil.getScoutEvent(this, mDbManager);
        if(scoutEvent != null) {
            getSupportActionBar().setSubtitle(scoutEvent.getName());
        }
    }

    private void setProgress(boolean toggle) {
        if (toggle)
            loadingMenuIcon.setActionView(R.layout.actionbar_progress);
        else loadingMenuIcon.setActionView(null);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            mSyncHandler.startScoutSync();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
