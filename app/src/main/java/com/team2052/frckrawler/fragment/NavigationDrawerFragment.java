package com.team2052.frckrawler.fragment;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.NavDrawerAdataper;
import com.team2052.frckrawler.listitems.ListItem;
import com.team2052.frckrawler.listitems.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerFragment extends Fragment {
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    public static final List<ListItem> NAV_ITEMS = new ArrayList<ListItem>();

    static {
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_scout, "Scout", R.drawable.ic_action_paste, R.layout.nav_list_item));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_server, "Server", R.drawable.ic_action_bluetooth, R.layout.nav_list_item));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_teams, "Teams", R.drawable.ic_action_group, R.layout.nav_list_item));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_users, "Users", R.drawable.ic_action_person, R.layout.nav_list_item));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_games, "Games", R.drawable.ic_action_event, R.layout.nav_list_item));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_options, "Options", R.drawable.ic_action_settings, R.layout.nav_list_item));
        //NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_settings, "Settings", R.drawable.ic_action_settings, R.layout.nav_list_item));
    }

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromSavedInstanceState = (savedInstanceState == null ? true : false);
        navAdapter = new NavDrawerAdataper(getActivity(), NAV_ITEMS);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        drawerListView = (ListView) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        drawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        drawerListView.setAdapter(navAdapter);

        return drawerListView;
    }

    private void selectItem(int position) {
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

    public boolean isDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(fragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, boolean encourageLearning, boolean useActionBarToggle) {
        fragmentContainerView = getActivity().findViewById(fragmentId);
        this.drawerLayout = drawerLayout;
        this.useActionBarToggle = useActionBarToggle;
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (this.useActionBarToggle) {
            ActionBar actionBar = getActivity().getActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            drawerToggle = new ActionBarDrawerToggle(
                    getActivity(), /* host Activity */
                    this.drawerLayout, /* DrawerLayout object */
                    R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
                    R.string.drawer_open, /* "open drawer" description for accessibility */
                    R.string.drawer_close /* "close drawer" description for accessibility */
            ) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    NavigationDrawerFragment.this.listener.onNavDrawerClosed();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerOpened(View drawerView) {
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
            this.drawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    NavigationDrawerFragment.this.drawerToggle.syncState();
                }
            });
        } else {
            drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerOpened(View drawerView) {
                    if (!isAdded()) {
                        return;
                    }
                    if (!NavigationDrawerFragment.this.userLearnedDrawer) {
                        NavigationDrawerFragment.this.userLearnedDrawer = true;
                        SharedPreferences sp = PreferenceManager
                                .getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    }
                    NavigationDrawerFragment.this.listener.onNavDrawerOpened();
                    getActivity().invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof NavigationDrawerListener){
            this.listener = (NavigationDrawerListener) activity;
        } else {
            throw new IllegalStateException("Activities must implement NavigationDrawerListener");
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (drawerLayout != null && isDrawerOpen()) {
            ActionBar actionBar = getActivity().getActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
            actionBar.setTitle(R.string.app_name);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (useActionBarToggle && drawerToggle != null) {
            return drawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface NavigationDrawerListener {
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
