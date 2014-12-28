package com.team2052.frckrawler.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.ScoutPagerAdapter;
import com.team2052.frckrawler.bluetooth.scout.LoginHandler;
import com.team2052.frckrawler.bluetooth.scout.ScoutSyncHandler;
import com.team2052.frckrawler.events.scout.ScoutSyncCancelledEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncErrorEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncStartEvent;
import com.team2052.frckrawler.events.scout.ScoutSyncSuccessEvent;
import com.team2052.frckrawler.listitems.items.NavDrawerItem;
import com.team2052.frckrawler.util.BluetoothUtil;

import de.greenrobot.event.EventBus;

/**
 * @author adam
 * @since 12/27/14.
 */
public class ScoutActivity extends ViewPagerActivity {

    private final int REQUEST_ENABLE_BT = 0;
    private ScoutSyncHandler mSyncHandler;
    private MenuItem loadingMenuIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mSyncHandler = ScoutSyncHandler.getInstance(this);

        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public PagerAdapter setAdapter() {
        return new ScoutPagerAdapter(getSupportFragmentManager());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scout_main_sync, menu);
        loadingMenuIcon = menu.findItem(R.id.menu_sync);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncCancelledEvent event) {
        setProgress(false);
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        int id = item.getId();
        if (id != R.id.nav_item_scout) {
            HomeActivity.newInstance(this, id);
        }
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
        setNavigationDrawerItemSelected(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
                if (BluetoothUtil.hasBluetoothAdapter()) {
                    if (!BluetoothUtil.isBluetoothEnabled()) {
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    } else {
                        mSyncHandler.startScoutSync();
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("unused")
    public void onEvent(ScoutSyncSuccessEvent event) {
        setProgress(false);
        LoginHandler loginHandler = LoginHandler.getInstance(this, mDaoSession);
        if (!loginHandler.isLoggedOn()) {
            loginHandler.login();
        } else if (!loginHandler.loggedOnUserStillExists()) {
            loginHandler.login();
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
