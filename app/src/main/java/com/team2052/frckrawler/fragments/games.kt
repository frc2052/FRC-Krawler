package com.team2052.frckrawler.fragments

import android.app.Application
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.SeasonInfoActivity
import com.team2052.frckrawler.adapters.items.smart.SeasonItemView
import com.team2052.frckrawler.adapters.items.smart.SmartAdapterInteractions
import com.team2052.frckrawler.getDatabase
import com.team2052.frckrawler.models.Season
import io.nlopez.smartadapters.SmartAdapter
import kotlinx.android.synthetic.main.recycler_view_fab.*
import rx.Observable

class SeasonsFragment : RecyclerFragment<SeasonsFragmentViewModel>() {
    override val viewModelClass: Class<SeasonsFragmentViewModel> = SeasonsFragmentViewModel::class.java
    override val showDividers: Boolean = true

    override fun provideAdapterCreator(creator: SmartAdapter.MultiAdaptersCreator) {
        val act = activity ?: return
        creator.map(Season::class.java, SeasonItemView::class.java)
        creator.listener { actionId, item, position, view ->
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item is Season) {
                startActivity(SeasonInfoActivity.newInstance(act, item.id))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater.inflate(R.layout.recycler_view_fab, null, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val ctx = context ?: return

        super.onViewCreated(view, savedInstanceState)
        fab.setOnClickListener {
            //When we click the add button add a game
            MaterialDialog.Builder(ctx)
                    .title(R.string.add_season)
                    .negativeText(android.R.string.cancel)
                    .positiveText(android.R.string.ok)
                    .inputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                    .input(R.string.season_name, 0, false) { _, input ->
                        val name = input.toString()
                        val game = Season(null, name)
                        viewModel.getDatabase().seasonsTable.insert(game)
                        viewModel.loadData()
                    }
                    .show()
        }
    }
}

class SeasonsFragmentViewModel(application: Application) : RecyclerFragmentViewModel<Season>(application) {
    override val dataObservable: Observable<List<Season>> = getDatabase().allGames()
}