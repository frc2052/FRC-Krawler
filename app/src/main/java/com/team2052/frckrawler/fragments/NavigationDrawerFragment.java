package com.team2052.frckrawler.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.team2052.frckrawler.Constants;
import com.team2052.frckrawler.R;
import com.team2052.frckrawler.adapters.NavDrawerAdataper;
import com.team2052.frckrawler.adapters.items.ListItem;
import com.team2052.frckrawler.adapters.items.items.NavDrawerItem;

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

    public void setItemSelected(int itemId) {
        if (mDrawerListView != null) {
            int position = navAdapter.getPositionForId(itemId);
            mDrawerListView.setItemChecked(position, true);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout, boolean encourageLearning, boolean useActionBarToggle) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        this.mDrawerLayout = drawerLayout;
        this.mUseActionBarToggle = useActionBarToggle;
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        if (this.mUseActionBarToggle) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            mDrawerToggle = new ActionBarDrawerToggle(getActivity(), this.mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
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

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    if (!isAdded()) {
                        return;
                    }
                    NavigationDrawerFragment.this.mListener.onNavDrawerClosed();
                    getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                }
            };
            this.mDrawerLayout.addDrawerListener(this.mDrawerToggle);
            this.mDrawerLayout.post(() -> NavigationDrawerFragment.this.mDrawerToggle.syncState());
        } else {
            drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
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
        SharedPreferences scoutPrefs = getActivity().getSharedPreferences(Constants.PREFS_FILE_NAME, 0);
        //Deny the scout to access all the items
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_scout, getActivity().getString(R.string.scout), R.drawable.ic_assignment_black_24dp));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_server, getActivity().getString(R.string.server), R.drawable.ic_bluetooth_black_24dp));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_teams, getActivity().getString(R.string.teams), R.drawable.ic_group_black_24dp));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_games, getActivity().getString(R.string.seasons), R.drawable.ic_season_black_24dp));
        NAV_ITEMS.add(new NavDrawerItem(R.id.nav_item_settings, getActivity().getString(R.string.settings), R.drawable.ic_settings_black_24dp));

        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        fromSavedInstanceState = (savedInstanceState == null);
        navAdapter = new NavDrawerAdataper(getActivity(), NAV_ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDrawerContainer = (RelativeLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) mDrawerContainer.findViewById(R.id.left_drawer);
        mDrawerListView.setOnItemClickListener((parent, view, position, id) -> selectItem(position));
        mDrawerListView.setAdapter(navAdapter);
        return mDrawerContainer;
    }

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

    public void onInsetsChanged(Rect insets) {
        if (getView() != null) {
            LinearLayout accountDetailsContainer = (LinearLayout) getView().findViewById(R.id.banner_details_container);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) accountDetailsContainer.getLayoutParams();
            lp.topMargin = insets.top;
            accountDetailsContainer.setLayoutParams(lp);
        }

    }

    public interface NavigationDrawerListener {
        void onNavDrawerItemClicked(NavDrawerItem item);

        void onNavDrawerOpened();

        void onNavDrawerClosed();
    }
}
