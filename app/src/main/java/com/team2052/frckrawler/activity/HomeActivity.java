package com.team2052.frckrawler.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.fragment.GamesFragment;
import com.team2052.frckrawler.fragment.OptionsFragment;
import com.team2052.frckrawler.fragment.ServerFragment;
import com.team2052.frckrawler.fragment.TeamsFragment;
import com.team2052.frckrawler.fragment.UsersFragment;
import com.team2052.frckrawler.fragment.scout.ScoutFragment;
import com.team2052.frckrawler.listitems.NavDrawerItem;

public class HomeActivity extends BaseActivity
{
    private static final String REQUESTED_MODE = "requested_mode";
    private static final String STATE_SELECTED_NAV_ID = "selected_navigation_drawer_position";
    private int mCurrentSelectedNavigationItemId;
    private boolean mFromSavedInstanceState;

    public static Intent newInstance(Context context, int requestedMode)
    {
        Intent i = new Intent(context, HomeActivity.class);
        i.putExtra(REQUESTED_MODE, requestedMode);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        boolean mIsScout = sharedPreferences.getBoolean(GlobalValues.IS_SCOUT_PREF, false);

        setContentView(R.layout.activity_home);
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
    }

    @Override
    public void onCreateNavigationDrawer()
    {
        useActionBarToggle();
        encourageLearning(!mFromSavedInstanceState);
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item)
    {
        int id = item.getId();
        if (id != mCurrentSelectedNavigationItemId) {
            switchToModeForId(id);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setNavigationDrawerItemSelected(mCurrentSelectedNavigationItemId);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if (!isDrawerOpen()) {
            //Reset the action bar
            ActionBar bar = getActionBar();
            if (bar != null) {
                bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                bar.setDisplayShowCustomEnabled(false);
                bar.setDisplayShowTitleEnabled(true);
            }
            assert getActionBar() != null;
            switch (mCurrentSelectedNavigationItemId) {
                case R.id.nav_item_scout:
                    getActionBar().setTitle("Scout");
                    break;
                case R.id.nav_item_server:
                    getActionBar().setTitle("Server");
                    break;
                case R.id.nav_item_teams:
                    getActionBar().setTitle("Teams");
                    break;
                case R.id.nav_item_users:
                    getActionBar().setTitle("Users");
                    break;
                case R.id.nav_item_games:
                    getActionBar().setTitle("Games");
                    break;
                case R.id.nav_item_options:
                    getActionBar().setTitle("Options");
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void switchToModeForId(int id)
    {
        Fragment fragment = null;
        switch (id) {
            case R.id.nav_item_scout:
                fragment = new ScoutFragment();
                break;
            case R.id.nav_item_server:
                fragment = new ServerFragment();
                break;
            case R.id.nav_item_teams:
                fragment = new TeamsFragment();
                break;
            case R.id.nav_item_users:
                fragment = new UsersFragment();
                break;
            case R.id.nav_item_games:
                fragment = new GamesFragment();
                break;
            case R.id.nav_item_options:
                fragment = new OptionsFragment();
                break;
        }
        assert fragment != null;
        fragment.setRetainInstance(true);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_in_top, R.anim.slide_out_bottom).replace(R.id.content, fragment, "mainFragment").commit();
        mCurrentSelectedNavigationItemId = id;
    }
}
