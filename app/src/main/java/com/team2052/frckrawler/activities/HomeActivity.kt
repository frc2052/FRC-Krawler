package com.team2052.frckrawler.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.view.Menu
import butterknife.ButterKnife
import com.team2052.frckrawler.core.common.Constants
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.items.items.NavDrawerItem
import com.team2052.frckrawler.fragments.ServerFragment
import com.team2052.frckrawler.fragments.TeamListFragment
import com.team2052.frckrawler.fragments.scout.ScoutHomeFragment
import com.team2052.frckrawler.services.ServerService
import kotlinx.android.synthetic.main.layout_toolbar.*

class HomeActivity : DatabaseActivity() {
    private var mCurrentSelectedNavigationItemId: Int = 0
    private var mFromSavedInstanceState = false

    override fun onNavDrawerItemClicked(item: NavDrawerItem) {
        val id = item.id
        if (id != mCurrentSelectedNavigationItemId) {
            handler.postDelayed({ switchToModeForId(id) }, NavigationDrawerActivity.DRAWER_CLOSE_ANIMATION_DURATION.toLong())
        }
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAV_ID, mCurrentSelectedNavigationItemId)
        val subFragment = supportFragmentManager.findFragmentById(R.id.container)
        subFragment?.onSaveInstanceState(outState)
    }

    override fun onCreateNavigationDrawer() {
        useActionBarToggle(true)
        encourageLearning(!mFromSavedInstanceState)
    }

    private fun switchToModeForId(id: Int) {
        var fragment: Fragment? = null
        when (id) {
            R.id.nav_item_scout -> fragment = ScoutHomeFragment()
            R.id.nav_item_server -> fragment = ServerFragment()
            R.id.nav_item_teams -> fragment = TeamListFragment()
            R.id.nav_item_metrics -> {
                startActivity(Intent(this, MetricsActivity::class.java))
                return
            }
            R.id.nav_item_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return
            }
        }
        if (fragment != null) {
            fragment.retainInstance = true
            supportFragmentManager.beginTransaction().setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).replace(R.id.container, fragment, "mainFragment").commit()
            mCurrentSelectedNavigationItemId = id
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        ButterKnife.bind(this)

        setSupportActionBar(toolbar)

        ViewCompat.setElevation(toolbar, resources.getDimension(R.dimen.toolbar_elevation))

        val sharedPreferences = getSharedPreferences(Constants.PREFS_FILE_NAME, 0)
        val mIsScout = sharedPreferences.getBoolean(Constants.IS_SCOUT_PREF, false)
        var initNavId = if (mIsScout) R.id.nav_item_scout else R.id.nav_item_server

        //Used to switch to a different fragment if it came from a separate activity
        val b = intent.extras
        if (b != null) {
            if (b.containsKey(REQUESTED_MODE)) {
                if (b.getInt(REQUESTED_MODE, -1) != -1) {
                    initNavId = b.getInt(REQUESTED_MODE)
                }
            }
        }

        if (savedInstanceState != null) {
            mFromSavedInstanceState = true
            if (savedInstanceState.containsKey(STATE_SELECTED_NAV_ID)) {
                mCurrentSelectedNavigationItemId = savedInstanceState.getInt(STATE_SELECTED_NAV_ID)
            }
        } else {
            switchToModeForId(initNavId)
        }

        //Start the service so it keeps in process
        applicationContext.startService(Intent(this, ServerService::class.java))
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onResume() {
        super.onResume()
        setNavigationDrawerItemSelected(mCurrentSelectedNavigationItemId)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val bar = supportActionBar
        if (bar != null) {
            bar.setDisplayShowCustomEnabled(false)
            bar.setDisplayShowTitleEnabled(true)

            when (mCurrentSelectedNavigationItemId) {
                R.id.nav_item_scout -> bar.setTitle(R.string.scout)
                R.id.nav_item_server -> bar.setTitle(R.string.server)
                R.id.nav_item_teams -> bar.setTitle(R.string.teams)
                R.id.nav_item_metrics -> bar.setTitle(R.string.metrics)
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    companion object {
        private val REQUESTED_MODE = "requested_mode"
        private val STATE_SELECTED_NAV_ID = "selected_navigation_drawer_position"

        fun newInstance(context: Context, requestedMode: Int): Intent {
            val i = Intent(context, HomeActivity::class.java)
            i.putExtra(REQUESTED_MODE, requestedMode)
            return i
        }
    }
}
