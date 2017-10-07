package com.team2052.frckrawler.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.tab.TeamInfoPagerAdapter
import com.team2052.frckrawler.bindToViewPagerAndTabLayout
import kotlinx.android.synthetic.main.layout_tab.*

/**
 * @author Adam
 */
class TeamInfoActivity : DatabaseActivity() {
    companion object {

        fun newInstance(context: Context, team_number: Long): Intent {
            val intent = Intent(context, TeamInfoActivity::class.java)
            intent.putExtra(DatabaseActivity.PARENT_ID, team_number)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tab)
        val team_id = intent.getLongExtra(DatabaseActivity.PARENT_ID, 0)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setActionBarSubtitle(team_id.toString())

        TeamInfoPagerAdapter(this, supportFragmentManager, team_id).bindToViewPagerAndTabLayout(view_pager, tab_layout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun inject() {
        component.inject(this)
    }
}
