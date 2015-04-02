package com.team2052.frckrawler.core.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Rect;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.team2052.frckrawler.R;
import com.team2052.frckrawler.core.GlobalValues;
import com.team2052.frckrawler.core.adapters.NavDrawerAdataper;
import com.team2052.frckrawler.core.listitems.ListItem;
import com.team2052.frckrawler.core.listitems.items.NavDrawerItem;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerFragment extends Fragment {
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    public final List<ListItem> NAV_ITEMS = new ArrayList<>();
    private NavDrawerAdataper navAdapter;
    private ListView mDrawerListView;
    private View mFragmentContainerView;
    private DrawerLayout mDrawerLayout;
    private boolean mUseActionBarToggle;
    private ActionBarDrawerToggle mDrawerToggle;
    private boolean mUserLearnedDrawer;
    private NavigationDrawerListener mListener;
    private boolean fromSavedInstanceState;
    private RelativeLayout mDrawerContainer;

    private void selectItem(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
            navAdapter.setItemSelected(position);
        }
        NavDrawerItem item = navAdapter.getItem(position);
        mListener.onNavDrawerItemClicked(item);
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public void setItemSelected(int itemId) {
        if (mDrawerListView != null) {
            int position = navAdapter.getPositionForId(itemId);
            mDrawerListView.setItemChecked(position, true);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, boolean encourageLearning, boolean useActionBarToggle, Toolbar toolbar) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        this.mDrawerLayout = drawerLayout;
        this.mUseActionBarToggle = useActionBarToggle;
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (this.mUseActionBarToggle) {
            ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(getActivity(), this.mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    NavigationDrawerFragment.this.mListener.onNavDrawerClosed();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }

                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    if (!mUserLearnedDrawer) {
                        mUserLearnedDrawer = true;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    }
                    NavigationDrawerFragment.this.mListener.onNavDrawerOpened();
                    getActivity().invalidateOptionsMenu();
                }
            };
            this.mDrawerLayout.setDrawerListener(this.mDrawerToggle);
            this.mDrawerLayout.post(new Runnable() {
                @Override
                public void run() {
                    NavigationDrawerFragment.this.mDrawerToggle.syncState();
                }
            });
        } else {
            drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                @Override
                public void onDrawerOpened(View drawerView) {
                    if (!isAdded()) {
                        return;
                    }
                    if (!NavigationDrawerFragment.this.mUserLearnedDrawer) {
                        NavigationDrawerFragment.this.mUserLearnedDrawer = true;
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                    }
                    NavigationDrawerFragment.this.mListener.onNavDrawerOpened();
                    getActivity().invalidateOptionsMenu();
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    if (!isAdded()) {
                        return;
                    }
                    NavigationDrawerFragment.this.mListener.onNavDrawerClosed();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }
            });
        }
        if (encourageLearning && !mUserLearnedDrawer && !fromSavedInstanceState) {
            this.mDrawerLayout.openDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof NavigationDrawerListener) {
            this.mListener = (NavigationDrawerListener) activity;
        } else {
            throw new IllegalStateException("Activities must implement NavigationDrawerListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(GlobalValues.PREFS_FILE_NAME, 0);
        //Deny the scout to access all the items
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_scout, getActivity().getString(R.string.scout), R.drawable.ic_assignment_black_24dp));

        if (!scoutPrefs.getBoolean(GlobalValues.IS_SCOUT_PREF, false)) {
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_server, getActivity().getString(R.string.server), R.drawable.ic_bluetooth_black_24dp));
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_teams, getActivity().getString(R.string.teams), R.drawable.ic_group_black_24dp));
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_users, getActivity().getString(R.string.users), R.drawable.ic_person_black_24dp));
            NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_games, getActivity().getString(R.string.games), R.drawable.ic_event_black_24dp));
        }

        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        fromSavedInstanceState = (savedInstanceState == null);
        navAdapter = new NavDrawerAdataper(getActivity(), NAV_ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerContainer = (RelativeLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) mDrawerContainer.findViewById(R.id.left_drawer);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(navAdapter);
        return mDrawerContainer;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mUseActionBarToggle && mDrawerToggle != null) {
            return mDrawerToggle.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when the insets of the nav drawer are changed. This allows us to properly place the contents so
     * that they don't flow under the status bar.
     */
    public void onInsetsChanged(Rect insets) {
        if (getView() != null) {
            RelativeLayout accountDetailsContainer = (RelativeLayout) getView().findViewById(R.id.banner_details_container);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) accountDetailsContainer.getLayoutParams();
            lp.topMargin = insets.top;
            accountDetailsContainer.setLayoutParams(lp);
        }

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
