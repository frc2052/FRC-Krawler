package com.team2052.frckrawler.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import butterknife.ButterKnife
import com.team2052.frckrawler.R
import com.team2052.frckrawler.fragments.scout.ScoutMatchFragment
import com.team2052.frckrawler.fragments.scout.ScoutPitFragment
import com.team2052.frckrawler.helpers.metric.MetricHelper
import com.team2052.frckrawler.interfaces.HasComponent

class ScoutActivity : DatabaseActivity(), HasComponent {
    private var fragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNavigationDrawerEnabled(false)
        setContentView(R.layout.activity_scout)
        ButterKnife.bind(this)

        val scout_type = intent.getIntExtra(SCOUT_TYPE_EXTRA, 0)

        if (fragment == null) {
            when (scout_type) {
                PIT_SCOUT_TYPE -> fragment = ScoutPitFragment.newInstance()
                PRACTICE_MATCH_SCOUT_TYPE -> fragment = ScoutMatchFragment.newInstance(MetricHelper.MATCH_PRACTICE_TYPE)
            //MATCH_SCOUT_TYPE,
                else -> fragment = ScoutMatchFragment.newInstance(MetricHelper.MATCH_GAME_TYPE)
            }
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.commit()
    }

    override fun onBackPressed() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Leave Scouting?")
        alertDialog.setMessage("Are you sure you want to leave scouting? All unsaved data will be lost.")
        alertDialog.setPositiveButton("I'm sure") { dialog, which -> super.onBackPressed() }
        alertDialog.setNegativeButton("No, I don't", null)
        alertDialog.create().show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun inject() {
        component.inject(this)
    }

    companion object {

        val MATCH_SCOUT_TYPE = 0
        val PIT_SCOUT_TYPE = 1
        val PRACTICE_MATCH_SCOUT_TYPE = 2
        private val SCOUT_TYPE_EXTRA = "com.team2052.frckrawler.SCOUT_TYPE_EXTRA"
        private val EVENT_ID_EXTRA = "com.team2052.frckrawler.EVENT_ID_EXTRA"

        fun newInstance(context: Context, type: Int): Intent {
            val intent = Intent(context, ScoutActivity::class.java)
            intent.putExtra(SCOUT_TYPE_EXTRA, type)
            return intent
        }
    }
}
