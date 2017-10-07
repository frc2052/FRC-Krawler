package com.team2052.frckrawler.fragments

import android.app.Activity.RESULT_OK
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import com.team2052.frckrawler.FRCKrawler
import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.EventInfoActivity
import com.team2052.frckrawler.activities.ScoutActivity
import com.team2052.frckrawler.activities.SeasonInfoActivity
import com.team2052.frckrawler.activities.ServerLogActivity
import com.team2052.frckrawler.bluetooth.server.ServerStatus
import com.team2052.frckrawler.getDatabase
import com.team2052.frckrawler.helpers.SnackbarHelper
import com.team2052.frckrawler.helpers.getDefaultBluetoothAdapter
import com.team2052.frckrawler.helpers.getDefaultBluetoothAdapterOrNull
import com.team2052.frckrawler.models.Event
import com.team2052.frckrawler.services.ServerService
import com.team2052.frckrawler.services.ServerServiceConnection
import com.team2052.frckrawler.views.MessageCardView
import kotlinx.android.synthetic.main.fragment_server.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subjects.BehaviorSubject
import rx.subscriptions.CompositeSubscription

class ServerFragment : BaseLifeCycleFragment<ServerFragmentViewModel>(), View.OnClickListener {
    private val REQUEST_BT_ENABLE: Int = 0
    override val viewModelClass: Class<ServerFragmentViewModel> = ServerFragmentViewModel::class.java

    override fun onClick(view: View) {
        doIfEventNotNull {
            when (view.id) {
                R.id.view_event -> startActivity(EventInfoActivity.newInstance(context, it.id))
                R.id.view_game -> startActivity(SeasonInfoActivity.newInstance(context, it.season_id))
                R.id.scout_match_button -> startActivity(ScoutActivity.newInstance(context, it, ScoutActivity.MATCH_SCOUT_TYPE))
                R.id.scout_pit_button -> startActivity(ScoutActivity.newInstance(context, it, ScoutActivity.PIT_SCOUT_TYPE))
            }
        }
        when (view.id) {
            R.id.view_logs -> startActivity(Intent(context, ServerLogActivity::class.java))
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater!!.inflate(R.layout.fragment_server, null, false)

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        lifecycle.addObserver(viewModel)

        viewModel.onEventsUpdated {
            if (it.isEmpty()) {
                message_card.messageType = MessageCardView.ERROR
                message_card.setMessageTitle("No Events")
                message_card.setMessageText("Please download some events")
            }

            message_card.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            scout_match_button.isEnabled = !it.isEmpty()
            scout_pit_button.isEnabled = !it.isEmpty()
            host_toggle.isEnabled = !it.isEmpty()
            view_event.isEnabled = !it.isEmpty()
            view_game.isEnabled = !it.isEmpty()

            val strings = it.map { it.name }
            val adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, strings)
            event_spinner.adapter = adapter

            viewModel.onServiceLoaded {
                onServerStatusChanged {
                    host_toggle.setOnCheckedChangeListener(null)
                    host_toggle.isChecked = it.state
                    host_toggle.setOnCheckedChangeListener({ _: CompoundButton?, b: Boolean ->
                        getDefaultBluetoothAdapterOrNull({
                            //Device Present
                            toggleServer(event_spinner.selectedItemPosition, b)
                        }, {
                            //No Bluetooth
                            SnackbarHelper.make(getView(), resources.getString(R.string.bluetooth_not_supported_message), Snackbar.LENGTH_LONG).show()
                        })
                    })
                    event_spinner.setSelection(it.findEventIndex(viewModel.getEvents()))
                }
            }
        }

        view_event.setOnClickListener(this)
        view_logs.setOnClickListener(this)
        view_game.setOnClickListener(this)
        scout_pit_button.setOnClickListener(this)
        scout_match_button.setOnClickListener(this)

        super.onViewCreated(view, savedInstanceState)
    }


    fun toggleServer(position: Int, state: Boolean) {
        if (getDefaultBluetoothAdapter().isEnabled) {
            viewModel.changeServerStatus(position, state)
        } else {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BT_ENABLE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BT_ENABLE && resultCode == RESULT_OK) {
            toggleServer(event_spinner.selectedItemPosition, true)
        }
    }

    fun getEventFromSelected(): Event? = viewModel.getEventAtIndex(event_spinner.selectedItemPosition)

    inline fun doIfEventNotNull(func: (Event) -> Unit) {
        val eventFromSelected = getEventFromSelected()
        if (eventFromSelected != null) {
            func(eventFromSelected)
        }
    }
}


class ServerFragmentViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {
    private val events = BehaviorSubject.create<List<Event>>()
    private var subscriptions = CompositeSubscription()
    private val serviceConnection = ServerServiceConnection()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun bindService() {
        getApplication<FRCKrawler>().bindService(Intent(getApplication(), ServerService::class.java), serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun onServiceLoaded(func: ServerFragmentViewModel.(ServerService) -> Unit) {
        val service = serviceConnection.getService()
        if (service != null) {
            func.invoke(this, service)
        } else {
            subscriptions.add(serviceConnection.toObservable().subscribe {
                func(it)
            })
        }
    }

    fun changeServerStatus(eventIndex: Int, status: Boolean) {
        val service = serviceConnection.getService()
        service?.changeServerStatus(events.value[eventIndex], status)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun loadEvents() {
        getDatabase().allEvents()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(events::onNext)
    }

    fun onServerStatusChanged(func: (ServerStatus) -> Unit) {
        subscriptions.add(serviceConnection.getService()!!.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(func))
    }

    fun getEventAtIndex(index: Int) = events.value[index]

    fun onEventsUpdated(func: (List<Event>) -> Unit) {
        subscriptions.add(events.observeOn(AndroidSchedulers.mainThread()).subscribe(func))
    }

    override fun onCleared() {
        subscriptions.unsubscribe()
    }

    fun getEvents(): List<Event> = events.value
}
