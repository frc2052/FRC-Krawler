package com.team2052.frckrawler.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.Menu;
import android.widget.FrameLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.fragment.NavigationDrawerFragment;
import com.team2052.frckrawler.listitems.NavDrawerItem;

/**
 * @author Adam
 */
public class NavigationDrawerActivity extends FragmentActivity implements NavigationDrawerFragment.NavigationDrawerListener {
    private static final String IS_DRAWER_OPEN = "is_drawer_open";
    private NavigationDrawerFragment mNavDrawerFragment;
    private DrawerLayout mDrawerLayout;
    private FrameLayout mContentView;
    private String mActionBarTitle;
    private boolean mUseActionBarToggle = false;
    private boolean mEncourageLearning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_navigation_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer_layout);
        // Call this so that subclasses can configure the navigation drawer before it is created
        onCreateNavigationDrawer();
        mNavDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);
        mNavDrawerFragment.setUp(R.id.navigation_drawer_fragment, (DrawerLayout) findViewById(R.id.nav_drawer_layout), mEncourageLearning, mUseActionBarToggle);
        mContentView = (FrameLayout) findViewById(R.id.content);
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
    public void onNavDrawerItemClicked(NavDrawerItem item) {
        int id = item.getId();
        TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, id)).startActivities();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isDrawerOpen()) {
            getActionBar().setTitle(R.string.app_name);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean isDrawerOpen() {
        return mNavDrawerFragment.isDrawerOpen();
    }

    @Override
    public void setContentView(int layoutResID) {
        mContentView.removeAllViews();
        getLayoutInflater().inflate(layoutResID, mContentView);
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

    public void setActionBarTitle(String title) {
        mActionBarTitle = title;
        if (!isDrawerOpen() && getActionBar() != null) {
            getActionBar().setTitle(mActionBarTitle);
        }
    }

    public void setActionBarTitle(int resID) {
        mActionBarTitle = getResources().getString(resID);
        if (!isDrawerOpen()) {
            getActionBar().setTitle(mActionBarTitle);
        }
    }

    public void setActionBarSubtitle(String subtitle) {
        if (!isDrawerOpen() && getActionBar() != null) {
            getActionBar().setSubtitle(subtitle);
        }
    }

    @Override
    public void onNavDrawerClosed() {
        if (mActionBarTitle != null) {
            getActionBar().setTitle(mActionBarTitle);
        }
    }

    public void useActionBarToggle() {
        mUseActionBarToggle = true;
    }

    @Override
    public void onNavDrawerOpened() {
        getActionBar().setTitle(R.string.app_name);
    }

    public void encourageLearning(boolean encourage) {
        mEncourageLearning = encourage;
    }
}
