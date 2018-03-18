package com.team2052.frckrawler.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.TeamInfoActivity
import com.team2052.frckrawler.adapters.items.smart.SmartAdapterInteractions
import com.team2052.frckrawler.adapters.items.smart.TeamItemView
import com.team2052.frckrawler.fragments.dialog.AddTeamDialogFragment
import com.team2052.frckrawler.getDatabase
import com.team2052.frckrawler.models.Team
import io.nlopez.smartadapters.SmartAdapter
import kotlinx.android.synthetic.main.recycler_view_fab.*
import rx.Observable

class TeamListFragment : RecyclerFragment<TeamListViewModel>() {
    override val showDividers: Boolean = true
    override val viewModelClass: Class<TeamListViewModel> = TeamListViewModel::class.java

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recycler_view_fab, null, false)
    }

    override fun provideAdapterCreator(creator: SmartAdapter.MultiAdaptersCreator) {
        val act = activity ?: return

        creator.map(Team::class.java, TeamItemView::class.java)
        fab.setOnClickListener {
            //TODO add event importer
            AddTeamDialogFragment.newInstance().show(fragmentManager, "addTeams")
        }
        creator.listener { actionId, item, position, view ->
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item is Team) {
                startActivity(TeamInfoActivity.newInstance(act, item.number))
            }
        }
    }
}

class TeamListViewModel(application: Application) : RecyclerFragmentViewModel<Team>(application) {
    override val dataObservable: Observable<List<Team>> = getDatabase().allTeams()
}
