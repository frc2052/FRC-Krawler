package com.team2052.frckrawler.activities;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.bluetooth.server.ServerService;
import com.team2052.frckrawler.fragments.NavigationDrawerFragment;
import com.team2052.frckrawler.listitems.items.NavDrawerItem;
import com.team2052.frckrawler.views.ScrimInsetsFrameLayout;

/**
 * @author Adam
 */
public class NavigationDrawerActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerListener {
    private static final String IS_DRAWER_OPEN = "is_drawer_open";
    protected DrawerLayout mDrawerLayout;
    protected FrameLayout mContentView;
    protected ScrimInsetsFrameLayout drawerContainer;
    private NavigationDrawerFragment mNavDrawerFragment;
    private String mActionBarTitle;
    private boolean mUseActionBarToggle = false;
    private boolean mEncourageLearning = false;
    private String mActionBarSubTitle;

    public void setNavigationDrawerItemSelected(int itemId) {
        mNavDrawerFragment.setItemSelected(itemId);
    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        int id = item.getId();
        TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, id)).startActivities();
    }

    @Override
    public void onNavDrawerOpened() {
    }

    @Override
    public void onNavDrawerClosed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_navigation_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        drawerContainer = (ScrimInsetsFrameLayout) findViewById(R.id.navigation_drawer_fragment_container);
        mContentView = (FrameLayout) findViewById(R.id.content);

        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.primary_dark));
        Intent serverIntent = new Intent(this, ServerService.class);
        startService(serverIntent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onCreateNavigationDrawer();

        mNavDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);

        mNavDrawerFragment.setUp(R.id.navigation_drawer_fragment_container, (DrawerLayout) findViewById(R.id.nav_drawer_layout), mEncourageLearning, mUseActionBarToggle);

        drawerContainer.setOnInsetsCallback(new ScrimInsetsFrameLayout.OnInsetsCallback() {
            @Override
            public void onInsetsChanged(Rect insets) {
                mNavDrawerFragment.onInsetsChanged(insets);
            }
        });
        // Restore the state of the navigation drawer on rotation changes
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(IS_DRAWER_OPEN)) {
                if (savedInstanceState.getBoolean(IS_DRAWER_OPEN)) {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        }
    }

    public void onCreateNavigationDrawer() {

    }

    @Override
    public void setContentView(int layoutResID) {
        mContentView.removeAllViews();
        getLayoutInflater().inflate(layoutResID, mContentView);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        mContentView.removeAllViews();
        mContentView.addView(view, params);
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public NavigationDrawerFragment getDrawerFragment() {
        return mNavDrawerFragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_DRAWER_OPEN, isDrawerOpen());
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    public void setActionBarTitle(String title) {
        mActionBarTitle = title;
        if (!isDrawerOpen() && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mActionBarTitle);
        }
    }

    public void setActionBarTitle(int resID) {
        mActionBarTitle = getResources().getString(resID);
        if (!isDrawerOpen()) {
            getSupportActionBar().setTitle(mActionBarTitle);
        }
    }

    public void setActionBarSubtitle(String subtitle) {
        mActionBarSubTitle = subtitle;
        if (!isDrawerOpen() && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    public void useActionBarToggle() {
        mUseActionBarToggle = true;
    }

    public void encourageLearning(boolean encourage) {
        mEncourageLearning = encourage;
    }
}
