package com.team2052.frckrawler.fragments.team

import android.os.Bundle

import com.team2052.frckrawler.activities.DatabaseActivity
import com.team2052.frckrawler.di.subscribers.KeyValueListSubscriber
import com.team2052.frckrawler.fragments.ListViewFragment

import rx.Observable

class TeamInfoFragment : ListViewFragment<Map<String, String>, KeyValueListSubscriber>() {
    override fun inject() {
        mComponent.inject(this)
    }

    override fun getObservable(): Observable<out Map<String, String>> {
        val teamId = arguments?.getLong(DatabaseActivity.PARENT_ID, -1) ?: -1
        return rxDbManager.teamInfo(teamId)
    }

    companion object {
        fun newInstance(team_id: Long): TeamInfoFragment {
            val fragment = TeamInfoFragment()
            val bundle = Bundle()
            bundle.putLong(DatabaseActivity.PARENT_ID, team_id)
            fragment.arguments = bundle
            return fragment
        }
    }
}
