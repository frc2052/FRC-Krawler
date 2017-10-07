package com.team2052.frckrawler.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.MenuItem
import android.view.View
import com.team2052.frckrawler.R
import com.team2052.frckrawler.adapters.tab.GameInfoPagerAdapter
import com.team2052.frckrawler.interfaces.RefreshListener
import kotlinx.android.synthetic.main.layout_tab_fab.*

/**
 * @author Adam
 * *
 * @since 10/15/2014
 */
class SeasonInfoActivity : DatabaseActivity(), View.OnClickListener {
    private var mAdapter: GameInfoPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_tab_fab)

        floating_action_button.setOnClickListener(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mAdapter = GameInfoPagerAdapter(supportFragmentManager, intent.getLongExtra(DatabaseActivity.PARENT_ID, 0))
        view_pager.adapter = mAdapter
        tab_layout.setupWithViewPager(view_pager)


        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                if (position == 0) {
                    floating_action_button.hide()
                    floating_action_button.locked = true
                    (mAdapter?.getRegisteredFragment(0) as RefreshListener).refresh()
                } else {
                    floating_action_button.locked = false
                    floating_action_button.show()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun inject() {
        component.inject(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onClick(v: View) {
        mAdapter!!.onClick(v, view_pager.currentItem)
    }

    companion object {

        fun newInstance(context: Context, season_id: Long): Intent {
            val intent = Intent(context, SeasonInfoActivity::class.java)
            intent.putExtra(DatabaseActivity.PARENT_ID, season_id)
            return intent
        }
    }
}
