package com.team2052.frckrawler.fragments.game

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.crash.FirebaseCrash
import com.team2052.frckrawler.R
import com.team2052.frckrawler.di.subscribers.KeyValueListSubscriber
import com.team2052.frckrawler.fragments.ListViewFragment
import com.team2052.frckrawler.models.Season

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

const val GAME_ID = "GAME_ID"

class SeasonInfoFragment : ListViewFragment<Map<String, String>, KeyValueListSubscriber>() {
    private lateinit var mSeason: Season

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSeason = rxDbManager.seasonsTable.load(arguments?.getLong(GAME_ID)) ?: return
        setHasOptionsMenu(true)
    }

    override fun inject() {
        mComponent.inject(this)
    }

    override fun getObservable(): Observable<out Map<String, String>> {
        return rxDbManager.gameInfo(mSeason)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_delete_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val ctx = context ?: return false

        when (item.itemId) {
            R.id.menu_delete -> MaterialDialog.Builder(ctx)
                    .title(R.string.delete_season)
                    .positiveColorRes(R.color.red_800)
                    .positiveText(R.string.delete)
                    .negativeText(R.string.cancel)
                    .content(R.string.delete_season_message)
                    .onPositive { _, _ ->
                        Observable.just<Season>(mSeason)
                                .map { game ->
                                    rxDbManager.seasonsTable.delete(game)
                                    game
                                }
                                .observeOn(Schedulers.computation())
                                .subscribeOn(AndroidSchedulers.mainThread())
                                .subscribe({ }, { onError ->
                                    onError.printStackTrace()
                                    FirebaseCrash.report(onError)
                                }) { activity?.finish() }
                    }
                    .show()
            R.id.menu_edit -> MaterialDialog.Builder(ctx)
                    .title(R.string.edit_game)
                    .input(getString(R.string.season_name), mSeason.name, false) { materialDialog, charSequence ->
                        mSeason.name = charSequence.toString()
                        mSeason.update()
                    }.show()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        fun newInstance(season_id: Long): SeasonInfoFragment {
            val args = Bundle()
            args.putLong(GAME_ID, season_id)
            val fragment = SeasonInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
