package com.team2052.frckrawler.fragments.robot

import android.os.Bundle
import android.view.View
import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.DatabaseActivity
import com.team2052.frckrawler.activities.RobotActivity
import com.team2052.frckrawler.adapters.items.ListElement
import com.team2052.frckrawler.di.binding.NoDataParams
import com.team2052.frckrawler.di.subscribers.RobotListSubscriber
import com.team2052.frckrawler.fragments.ListViewFragment
import com.team2052.frckrawler.fragments.dialog.AddTeamToEventDialogFragment
import com.team2052.frckrawler.models.Robot
import rx.Observable

/**
 * Generic Fragment list for viewing robots from either a single team, or in an event
 * @author Adam
 */
class RobotsFragment : ListViewFragment<List<Robot>, RobotListSubscriber>(), View.OnClickListener {
    private var mViewType: Int = 0
    private var mKey: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.mViewType = arguments?.getInt(VIEW_TYPE, 0) ?: 0
        this.mKey = arguments?.getLong(DatabaseActivity.PARENT_ID) ?:0
    }

    override fun inject() {
        mComponent.inject(this)
    }

    override fun getObservable(): Observable<out List<Robot>> {
        return if (mViewType == 0) rxDbManager.robotsWithTeam(mKey) else rxDbManager.robotsAtEvent(mKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListView.setOnItemClickListener { parent, view1, position, id ->
            val act = activity ?: return@setOnItemClickListener
            startActivity(RobotActivity.newInstance(act, java.lang.Long.parseLong((parent.adapter.getItem(position) as ListElement).key)))
        }
    }

    override fun getNoDataParams(): NoDataParams {
        return NoDataParams("No teams found", R.drawable.ic_team)
    }

    override fun onClick(v: View) {
        if (v.id == R.id.floating_action_button) {
            val load = rxDbManager.eventsTable.load(mKey)
            if (load != null) {
                AddTeamToEventDialogFragment.newInstance(load).show(childFragmentManager, "addTeam")
            }
        }
    }

    companion object {
        val VIEW_TYPE = "VIEW_TYPE"

        //To create a valid instance view by team or by game
        fun newTeamInstance(team_id: Long): RobotsFragment {
            val fragment = RobotsFragment()
            val b = Bundle()
            b.putInt(VIEW_TYPE, 0)
            b.putLong(DatabaseActivity.PARENT_ID, team_id)
            fragment.arguments = b
            return fragment
        }

        fun newEventInstance(event_id: Long): RobotsFragment {
            val fragment = RobotsFragment()
            val b = Bundle()
            b.putInt(VIEW_TYPE, 1)
            b.putLong(DatabaseActivity.PARENT_ID, event_id)
            fragment.arguments = b
            return fragment
        }
    }
}
