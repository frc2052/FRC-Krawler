package com.team2052.frckrawler.activity;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.fragment.NavigationDrawerFragment;
import com.team2052.frckrawler.listitems.items.NavDrawerItem;
import com.team2052.frckrawler.view.ScrimInsetsFrameLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Adam
 */
public class NavigationDrawerActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerListener
{
    private static final String IS_DRAWER_OPEN = "is_drawer_open";
    private NavigationDrawerFragment mNavDrawerFragment;

    @InjectView(R.id.nav_drawer_layout)
    protected DrawerLayout mDrawerLayout;
    @InjectView(R.id.content)
    protected FrameLayout mContentView;

    @InjectView(R.id.navigation_drawer_fragment_container)
    protected ScrimInsetsFrameLayout drawerContainer;

    private String mActionBarTitle;
    private boolean mUseActionBarToggle = false;
    private boolean mEncourageLearning = false;
    private String mActionBarSubTitle;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_navigation_drawer);
        ButterKnife.inject(this);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.red900));
        // Call this so that subclasses can configure the navigation drawer before it is created
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        onCreateNavigationDrawer();

        mNavDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);

        mNavDrawerFragment.setUp(R.id.navigation_drawer_fragment_container, (DrawerLayout) findViewById(R.id.nav_drawer_layout), mEncourageLearning, mUseActionBarToggle, mToolbar);

        drawerContainer.setOnInsetsCallback(new ScrimInsetsFrameLayout.OnInsetsCallback()
        {
            @Override
            public void onInsetsChanged(Rect insets)
            {
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

    public void setNavigationDrawerItemSelected(int position)
    {
        mNavDrawerFragment.setItemSelected(position);
    }

    public void onCreateNavigationDrawer()
    {

    }

    @Override
    public void onNavDrawerItemClicked(NavDrawerItem item)
    {
        int id = item.getId();
        TaskStackBuilder.create(this).addNextIntent(HomeActivity.newInstance(this, id)).startActivities();
    }

    public boolean isDrawerOpen()
    {
        return mDrawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    @Override
    public void setContentView(int layoutResID)
    {
        mContentView.removeAllViews();
        getLayoutInflater().inflate(layoutResID, mContentView);
    }

    public DrawerLayout getDrawerLayout()
    {
        return mDrawerLayout;
    }

    public NavigationDrawerFragment getDrawerFragment()
    {
        return mNavDrawerFragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_DRAWER_OPEN, isDrawerOpen());
    }

    public void setActionBarTitle(String title)
    {
        mActionBarTitle = title;
        if (!isDrawerOpen() && getSupportActionBar() != null) {
            getSupportActionBar().setTitle(mActionBarTitle);
        }
    }

    public void setActionBarTitle(int resID)
    {
        mActionBarTitle = getResources().getString(resID);
        if (!isDrawerOpen()) {
            getSupportActionBar().setTitle(mActionBarTitle);
        }
    }

    public void setActionBarSubtitle(String subtitle)
    {
        mActionBarSubTitle = subtitle;
        if (!isDrawerOpen() && getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    @Override
    public void onNavDrawerClosed()
    {
    }

    public void useActionBarToggle()
    {
        mUseActionBarToggle = true;
    }

    @Override
    public void onNavDrawerOpened()
    {
    }


    public void encourageLearning(boolean encourage)
    {
        mEncourageLearning = encourage;
    }
}
