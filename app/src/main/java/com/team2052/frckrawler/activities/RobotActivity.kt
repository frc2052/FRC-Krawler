package com.team2052.frckrawler.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.tab.RobotViewPagerAdapter
import kotlinx.android.synthetic.main.layout_tab.*

class RobotActivity : DatabaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tab)

        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar?)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val robot = rxDbManager.robotsTable.load(intent.getLongExtra(DatabaseActivity.PARENT_ID, 0))

        if (robot == null) {
            finish()
            return
        }

        setActionBarSubtitle(robot.team_id.toString())
        view_pager.adapter = RobotViewPagerAdapter(this, supportFragmentManager, robot.id)
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun newInstance(context: Context, rKey: Long): Intent {
            val intent = Intent(context, RobotActivity::class.java)
            intent.putExtra(DatabaseActivity.PARENT_ID, rKey)
            return intent
        }
    }

}
