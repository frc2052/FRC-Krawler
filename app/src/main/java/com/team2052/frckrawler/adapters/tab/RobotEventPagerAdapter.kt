package com.team2052.frckrawler.adapters.tab

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.team2052.frckrawler.R
import com.team2052.frckrawler.fragments.robot.RobotEventSummaryFragment

class RobotEventPagerAdapter(context: Context, fm: FragmentManager, private val robot_id: Long, private val event_id: Long) : FragmentPagerAdapter(fm) {
    private val HEADERS: Array<String> = context.resources.getStringArray(R.array.robot_event_tab_titles)

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return RobotEventSummaryFragment.newInstance(robot_id, event_id)
        }/*case 1:
                return RobotEventMatchesFragment.newInstance(robot_id, event_id);*/
        return null
    }

    override fun getCount(): Int {
        return HEADERS.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return HEADERS[position]
    }
}
