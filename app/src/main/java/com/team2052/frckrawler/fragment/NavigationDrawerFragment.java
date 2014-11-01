package com.team2052.frckrawler.fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.team2052.frckrawler.GlobalValues;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.NavDrawerAdataper;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.items.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerFragment extends Fragment
{
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    public final List<ListItem> NAV_ITEMS = new ArrayList<>();
    private NavDrawerAdataper navAdapter;
    private ListView drawerListView;
    private View fragmentContainerView;
    private DrawerLayout drawerLayout;
    private boolean useActionBarToggle;
    private ActionBarDrawerToggle drawerToggle;
    private boolean userLearnedDrawer;
    private NavigationDrawerListener listener;
    private boolean fromSavedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        //Deny the scout to access all the items
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_scout, "Scout", R.drawable.ic_assignment_black_24dp));

        if (!scoutPrefs.getBoolean(GlobalValues.IS_SCOUT_PREF, false)) {
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_server, "Server", R.drawable.ic_bluetooth_black_24dp));
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_teams, "Teams", R.drawable.ic_group_black_24dp));
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_users, "Users", R.drawable.ic_person_black_24dp));
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_games, "Games", R.drawable.ic_event_black_24dp));
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_options, "Options", R.drawable.ic_settings_black_24dp));
        }

        userLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        fromSavedInstanceState = (savedInstanceState == null);
        navAdapter = new NavDrawerAdataper(getActivity(), NAV_ITEMS);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        drawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectItem(position);
            }
        });
        drawerListView.setAdapter(navAdapter);
        return drawerListView;
    }

    private void selectItem(int position)
    {
        if (drawerListView != null) {
            drawerListView.setItemChecked(position, true);
            navAdapter.setItemSelected(position);
        }
        NavDrawerItem item = navAdapter.getItem(position);
        listener.onNavDrawerItemClicked(item);
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(fragmentContainerView);
        }
    }

    public void setItemSelected(int itemId)
    {
        if (drawerListView != null) {
            int position = navAdapter.getPositionForId(itemId);
            drawerListView.setItemChecked(position, true);
        }
    }

    public boolean isDrawerOpen()
    {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, boolean encourageLearning, boolean useActionBarToggle, Toolbar toolbar)
    {
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;
        this.useActionBarToggle = useActionBarToggle;
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (this.useActionBarToggle) {
            ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            drawerToggle = new ActionBarDrawerToggle(getActivity(), this.drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close)
            {
                @Override
                public void onDrawerClosed(View drawerView)
                {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    NavigationDrawerFragment.this.listener.onNavDrawerClosed();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerOpened(View drawerView)
                {
                    super.onDrawerOpened(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    if (!userLearnedDrawer) {
                        userLearnedDrawer = true;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    }
                    NavigationDrawerFragment.this.listener.onNavDrawerOpened();
                    getActivity().invalidateOptionsMenu();
                }
            };
            this.drawerLayout.setDrawerListener(this.drawerToggle);
            this.drawerLayout.post(new Runnable()
            {
                @Override
                public void run()
                {
                    NavigationDrawerFragment.this.drawerToggle.syncState();
                }
            });
        } else {
            drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener()
            {
                @Override
                public void onDrawerOpened(View drawerView)
                {
                    if (!isAdded()) {
                        return;
                    }
                    if (!NavigationDrawerFragment.this.userLearnedDrawer) {
                        NavigationDrawerFragment.this.userLearnedDrawer = true;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    }
                    NavigationDrawerFragment.this.listener.onNavDrawerOpened();
                    getActivity().invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView)
                {
                    if (!isAdded()) {
                        return;
                    }
                    NavigationDrawerFragment.this.listener.onNavDrawerClosed();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }
            });
        }
        if (encourageLearning && !userLearnedDrawer && !fromSavedInstanceState) {
            this.drawerLayout.openDrawer(fragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        if (activity instanceof NavigationDrawerListener) {
            this.listener = (NavigationDrawerListener) activity;
        } else {
            throw new IllegalStateException("Activities must implement NavigationDrawerListener");
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu)
    {
        if (drawerLayout != null && isDrawerOpen()) {
            android.support.v7.app.ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(R.string.app_name);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (useActionBarToggle && drawerToggle != null) {
            return drawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDetach()
    {
        super.onDetach();
        listener = null;
    }

    public interface NavigationDrawerListener
    {
        /**
         * Called when a NavDrawerItem in the navigation drawer is clicked
         *
         * @param item The item that was clicked
         */
        public void onNavDrawerItemClicked(NavDrawerItem item);

        /**
         * Called when the drawer is opened.
         */
        public void onNavDrawerOpened();

        /**
         * CAlled when the drawer is opened.
         */
        public void onNavDrawerClosed();
    }
}
