package com.team2052.frckrawler.activities

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.tab.GameInfoPagerAdapter
import kotlinx.android.synthetic.main.layout_tab_fab.*

/**
 * @author Adam
 * *
 * @since 10/15/2014
 */
class MetricsActivity : DatabaseActivity(), View.OnClickListener {
    private var mAdapter: GameInfoPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setNavigationDrawerEnabled(false)
        setContentView(R.layout.layout_tab_fab)

        floating_action_button.setOnClickListener(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAdapter = GameInfoPagerAdapter(supportFragmentManager, intent.getLongExtra(DatabaseActivity.PARENT_ID, 0))
        view_pager.adapter = mAdapter
        tab_layout.setupWithViewPager(view_pager)
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onClick(v: View) {
        mAdapter!!.onClick(v, view_pager.currentItem)
    }
}
