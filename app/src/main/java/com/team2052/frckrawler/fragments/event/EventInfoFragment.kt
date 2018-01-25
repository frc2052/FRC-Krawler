package com.team2052.frckrawler.fragments.event

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.AppCompatEditText
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem

import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.NavigationDrawerActivity
import com.team2052.frckrawler.di.subscribers.KeyValueListSubscriber
import com.team2052.frckrawler.fragments.ListViewFragment
import com.team2052.frckrawler.helpers.Util
import com.team2052.frckrawler.interfaces.RefreshListener
import com.team2052.frckrawler.models.Event

import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class EventInfoFragment : ListViewFragment<Map<String, String>, KeyValueListSubscriber>(), RefreshListener {
    private var mEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        mEvent = rxDbManager.eventsTable.load(arguments?.getLong(EVENT_ID))
    }

    override fun inject() {
        mComponent.inject(this)
    }

    override fun getObservable(): Observable<out Map<String, String>> {
        return rxDbManager.eventInfo(mEvent)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.edit_delete_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.menu_delete -> buildDeleteDialog()?.show()
            R.id.menu_edit -> buildEditDialog()?.show()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun buildDeleteDialog(): AlertDialog? {
        val act = activity ?: return null

        val builder = AlertDialog.Builder(act).apply {
            setTitle("Delete Event?")
            setMessage("Are you sure you want to delete this event?")
            setPositiveButton("Ok") { dialog, which ->
                Observable.just<Event>(mEvent)
                        .map { event ->
                            rxDbManager.runInTx(Runnable { rxDbManager.eventsTable.delete(event) })
                            event
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe { activity?.finish() }
            }
            setNegativeButton("Cancel", null)
        }
        return builder.create()
    }

    private fun buildEditDialog(): AlertDialog? {
        val act = activity ?: return null

        val builder = AlertDialog.Builder(act).apply {
            val name = AppCompatEditText(activity)
            name.setText(mEvent!!.name)
            val padding = Util.getPixelsFromDp(activity, 16)
            name.setPadding(padding, padding, padding, padding)
            setView(name)
            setTitle("Edit Event")
            setPositiveButton("Ok") { _, _ ->
                mEvent?.let {
                    it.name = name.text.toString()
                    it.update()
                }
                (activity as NavigationDrawerActivity).setActionBarSubtitle(mEvent?.name)
            }
            setNegativeButton("Cancel", null)
        }
        return builder.create()
    }

    companion object {
        val EVENT_ID = "EVENT_ID"

        fun newInstance(event_id: Long): EventInfoFragment {
            val args = Bundle()
            args.putLong(EVENT_ID, event_id)
            val fragment = EventInfoFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
