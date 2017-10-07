package com.team2052.frckrawler.adapters.tab

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.team2052.frckrawler.R
import com.team2052.frckrawler.fragments.robot.RobotAttendingEventsFragment
import com.team2052.frckrawler.fragments.robot.RobotSummaryFragment

class RobotViewPagerAdapter(context: Context, fm: FragmentManager, private val robot_id: Long) : FragmentPagerAdapter(fm) {
    private val HEADERS: Array<String> = context.resources.getStringArray(R.array.robot_tab_titles)

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return RobotAttendingEventsFragment.newInstance(robot_id)
            1 -> return RobotSummaryFragment.newInstance(robot_id)
        }
        return null
    }

    override fun getCount(): Int {
        return HEADERS.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return HEADERS[position]
    }
}
