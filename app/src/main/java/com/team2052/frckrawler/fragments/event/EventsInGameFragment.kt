package com.team2052.frckrawler.fragments.event

import android.os.Bundle
import android.view.View

import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.DatabaseActivity
import com.team2052.frckrawler.activities.EventInfoActivity
import com.team2052.frckrawler.adapters.items.smart.EventItemView
import com.team2052.frckrawler.adapters.items.smart.SmartAdapterInteractions
import com.team2052.frckrawler.di.binding.NoDataParams
import com.team2052.frckrawler.di.binding.RecyclerViewBinder
import com.team2052.frckrawler.fragments.RecyclerViewFragment
import com.team2052.frckrawler.fragments.dialog.ImportDataSimpleDialogFragment
import com.team2052.frckrawler.models.Event

import io.nlopez.smartadapters.SmartAdapter
import rx.Observable

/**
 * Displays events from a game_id
 */
class EventsInGameFragment : RecyclerViewFragment<List<Event>, RecyclerViewBinder>(), View.OnClickListener {
    private var mGame_id: Long = 0

    override fun inject() {
        mComponent.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        mGame_id = arguments?.getLong(DatabaseActivity.PARENT_ID, 0) ?: 0
        super.onCreate(savedInstanceState)
    }

    override fun getObservable(): Observable<out List<Event>> {
        return rxDbManager.eventsByGame(mGame_id)
    }

    override fun getNoDataParams(): NoDataParams {
        return NoDataParams("No events found", R.drawable.ic_event)
    }

    override fun provideAdapterCreator(creator: SmartAdapter.MultiAdaptersCreator) {
        creator.map(Event::class.java, EventItemView::class.java)
        creator.listener { actionId, item, position, view ->
            if (actionId == SmartAdapterInteractions.EVENT_CLICKED && item is Event) {
                context?.let {
                    startActivity(EventInfoActivity.newInstance(it, item.id))
                }
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.floating_action_button) {
            ImportDataSimpleDialogFragment.newInstance(mGame_id).show(childFragmentManager, "importEvent")
        }
    }

    companion object {

        fun newInstance(game_id: Long): EventsInGameFragment {
            val fragment = EventsInGameFragment()
            val bundle = Bundle()
            bundle.putLong(DatabaseActivity.PARENT_ID, game_id)
            fragment.arguments = bundle
            return fragment
        }
    }
}
