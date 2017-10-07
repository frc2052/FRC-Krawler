package com.team2052.frckrawler.fragments.event

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View

import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.DatabaseActivity
import com.team2052.frckrawler.adapters.items.smart.MatchItemView
import com.team2052.frckrawler.di.binding.NoDataParams
import com.team2052.frckrawler.di.binding.RecyclerViewBinder
import com.team2052.frckrawler.fragments.RecyclerViewFragment
import com.team2052.frckrawler.fragments.dialog.UpdateMatchesProcessDialog
import com.team2052.frckrawler.models.Match

import io.nlopez.smartadapters.SmartAdapter
import rx.Observable

/**
 * @author Adam
 */
class MatchListFragment : RecyclerViewFragment<List<Match>, RecyclerViewBinder>() {

    private var mEvent_id: Long = 0

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.match_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item!!.itemId == R.id.update_schedule) {
            UpdateMatchesProcessDialog.newInstance(mEvent_id).show(childFragmentManager, "matchUpdateDialog")
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        mEvent_id = arguments.getLong(DatabaseActivity.PARENT_ID)
    }

    override fun showDividers(): Boolean {
        return false
    }

    override fun inject() {
        mComponent.inject(this)
    }

    override fun getObservable(): Observable<out List<Match>> {
        return rxDbManager.matchesAtEvent(mEvent_id)
    }

    override fun getNoDataParams(): NoDataParams {
        return NoDataParams("No matches found", R.drawable.ic_schedule_black_24dp)
    }

    override fun provideAdapterCreator(creator: SmartAdapter.MultiAdaptersCreator) {
        creator.map(Match::class.java, MatchItemView::class.java)
    }

    companion object {

        fun newInstance(event_id: Long): MatchListFragment {
            val fragment = MatchListFragment()
            val b = Bundle()
            b.putLong(DatabaseActivity.PARENT_ID, event_id)
            fragment.arguments = b
            return fragment
        }
    }
}
