package com.team2052.frckrawler.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.tab.RobotEventPagerAdapter
import com.team2052.frckrawler.bindToViewPagerAndTabLayout
import kotlinx.android.synthetic.main.layout_tab.*

class RobotEventActivity : DatabaseActivity() {
    override fun inject() {
        component.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tab)
        setSupportActionBar(toolbar)

        val robot_id = intent.getLongExtra(DatabaseActivity.PARENT_ID, 0)
        val event_id = intent.getLongExtra(EVENT_ID, 0)

        title = String.format("%d@%s", rxDbManager.robotsTable.load(robot_id)?.team_id, rxDbManager.eventsTable.load(event_id)?.name)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        RobotEventPagerAdapter(this, supportFragmentManager, robot_id, event_id).bindToViewPagerAndTabLayout(view_pager, tab_layout)
    }

    companion object {
        private val EVENT_ID = "EVENT_ID"

        fun newInstance(context: Context, robot_id: Long, event_id: Long): Intent {
            val intent = Intent(context, RobotEventActivity::class.java)
            intent.putExtra(DatabaseActivity.PARENT_ID, robot_id)
            intent.putExtra(EVENT_ID, event_id)
            return intent
        }
    }
}
