package com.team2052.frckrawler.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.tab.EventViewPagerAdapter
import com.team2052.frckrawler.bindToViewPagerAndTabLayout
import kotlinx.android.synthetic.main.layout_tab_fab.*

/**
 * @author Adam
 * *
 * @since 10/16/2014
 */
class EventInfoActivity : DatabaseActivity(), View.OnClickListener {
    var adapter: EventViewPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mEvent_id = intent.getLongExtra(DatabaseActivity.PARENT_ID, 0)
        setContentView(R.layout.layout_tab_fab)

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        floating_action_button.setOnClickListener(this)

        adapter = EventViewPagerAdapter(supportFragmentManager, mEvent_id)
        adapter?.bindToViewPagerAndTabLayout(view_pager, tab_layout)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageSelected(position: Int) {
                if (position < 3) {
                    floating_action_button.hide()
                    floating_action_button.locked = true
                } else {
                    floating_action_button.locked = false
                    floating_action_button.show()
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onClick(v: View) {
        adapter?.onClick(v, view_pager.currentItem)
    }

    companion object {

        fun newInstance(context: Context, event_id: Long): Intent {
            val intent = Intent(context, EventInfoActivity::class.java)
            intent.putExtra(DatabaseActivity.PARENT_ID, event_id)
            return intent
        }
    }
}
