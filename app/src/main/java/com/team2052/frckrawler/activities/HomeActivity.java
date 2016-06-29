package com.team2052.frckrawler.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.team2052.frckrawler.Constants;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.server.ServerService;
import com.team2052.frckrawler.fragments.GamesFragment;
import com.team2052.frckrawler.fragments.ScoutHomeFragment;
import com.team2052.frckrawler.fragments.ServerFragment;
import com.team2052.frckrawler.fragments.TeamsFragment;
import com.team2052.frckrawler.listitems.items.NavDrawerItem;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends DatabaseActivity {
    private static final String REQUESTED_MODE = "requested_mode";
    private static final String STATE_SELECTED_NAV_ID = "selected_navigation_drawer_position";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private int mCurrentSelectedNavigationItemId;
    private boolean mFromSavedInstanceState = false;

    public static Intent newInstance(Context context, int requestedMode) {
        Intent i = new Intent(context, HomeActivity.class);
        i.putExtra(REQUESTED_MODE, requestedMode);
        return i;
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        int id = item.getId();
        if (id != mCurrentSelectedNavigationItemId) {
            handler.postDelayed(() -> switchToModeForId(id), DRAWER_CLOSE_ANIMATION_DURATION);
        }
    }

    @Override
    public void onCreateNavigationDrawer() {
        useActionBarToggle(true);
        encourageLearning(!mFromSavedInstanceState);
    }

    private void switchToModeForId(int id) {
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_item_scout:
                fragment = new ScoutHomeFragment();
                break;
            case R.id.nav_item_server:
                fragment = new ServerFragment();
                break;
            case R.id.nav_item_teams:
                fragment = new TeamsFragment();
                break;
            case R.id.nav_item_games:
                fragment = new GamesFragment();
                break;
            case R.id.nav_item_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return;
        }
        assert fragment != null;
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.container, fragment, "mainFragment").commit();
        mCurrentSelectedNavigationItemId = id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ViewCompat.setElevation(toolbar, getResources().getDimension(R.dimen.toolbar_elevation));

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        boolean mIsScout = sharedPreferences.getBoolean(Constants.IS_SCOUT_PREF, false);
        int initNavId = mIsScout ? R.id.nav_item_scout : R.id.nav_item_server;

        //Used to switch to a different fragment if it came from a separate activity
        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey(REQUESTED_MODE))
                if (b.getInt(REQUESTED_MODE, -1) != -1) {
                    initNavId = b.getInt(REQUESTED_MODE);
                }
        }

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true;
            if (savedInstanceState.containsKey(STATE_SELECTED_NAV_ID)) {
                mCurrentSelectedNavigationItemId = savedInstanceState.getInt(STATE_SELECTED_NAV_ID);
            }
        } else {
            switchToModeForId(initNavId);
        }

        //Start the service so it keeps in process
        getApplicationContext().startService(new Intent(this, ServerService.class));
    }

    @Override
    public void inject() {
        getComponent().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNavigationDrawerItemSelected(mCurrentSelectedNavigationItemId);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowCustomEnabled(false);
            bar.setDisplayShowTitleEnabled(true);

            switch (mCurrentSelectedNavigationItemId) {
                case R.id.nav_item_scout:
                    bar.setTitle("Scout");
                    break;
                case R.id.nav_item_server:
                    bar.setTitle("Server");
                    break;
                case R.id.nav_item_teams:
                    bar.setTitle("Teams");
                    break;
                case R.id.nav_item_games:
                    bar.setTitle("Games");
                    break;
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }
}
