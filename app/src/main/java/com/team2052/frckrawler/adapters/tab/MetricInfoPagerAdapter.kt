package com.team2052.frckrawler.adapters.tab

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import com.team2052.frckrawler.fragments.metric.MetricInfoFragment

class MetricInfoPagerAdapter(fragmentManager: FragmentManager, internal val metricId: Long) : FragmentPagerAdapter(fragmentManager) {
    var headers = arrayOf("Info")

    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return MetricInfoFragment.newInstance(metricId)
        }
        return null
    }

    override fun getPageTitle(position: Int): CharSequence {
        return headers[position]
    }

    override fun getCount(): Int {
        return headers.size
    }
}
