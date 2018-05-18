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
import android.widget.CompoundButton
import com.team2052.frckrawler.FRCKrawler
import com.team2052.frckrawler.R
import com.team2052.frckrawler.activities.ScoutActivity
import com.team2052.frckrawler.activities.ServerLogActivity
import com.team2052.frckrawler.core.bluetooth.getDefaultBluetoothAdapter
import com.team2052.frckrawler.core.bluetooth.getDefaultBluetoothAdapterOrNull
import com.team2052.frckrawler.core.bluetooth.server.ServerStatus
import com.team2052.frckrawler.core.common.SnackbarHelper
import com.team2052.frckrawler.services.ServerService
import com.team2052.frckrawler.services.ServerServiceConnection
import kotlinx.android.synthetic.main.fragment_server.*
import rx.android.schedulers.AndroidSchedulers
import rx.subscriptions.CompositeSubscription

class ServerFragment : BaseLifeCycleFragment<ServerFragmentViewModel>(), View.OnClickListener {
    private val REQUEST_BT_ENABLE: Int = 0
    override val viewModelClass: Class<ServerFragmentViewModel> = ServerFragmentViewModel::class.java

    override fun onClick(view: View) {
        val ctx = context ?: return

        when (view.id) {
            R.id.scout_match_button -> startActivity(ScoutActivity.newInstance(ctx, ScoutActivity.MATCH_SCOUT_TYPE))
            R.id.scout_pit_button -> startActivity(ScoutActivity.newInstance(ctx, ScoutActivity.PIT_SCOUT_TYPE))
        }

        when (view.id) {
            R.id.view_logs -> startActivity(Intent(ctx, ServerLogActivity::class.java))
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = inflater.inflate(R.layout.fragment_server, null, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycle.addObserver(viewModel)

        viewModel.onServiceLoaded {
            onServerStatusChanged {
                host_toggle.setOnCheckedChangeListener(null)
                host_toggle.isChecked = it.state
                host_toggle.setOnCheckedChangeListener({ _: CompoundButton?, b: Boolean ->
                    getDefaultBluetoothAdapterOrNull({
                        //Device Present
                        toggleServer(b)
                    }, {
                        //No Bluetooth
                        SnackbarHelper.make(getView(), resources.getString(R.string.bluetooth_not_supported_message), Snackbar.LENGTH_LONG).show()
                    })
                })
            }
        }

        view_logs.setOnClickListener(this)
        scout_pit_button.setOnClickListener(this)
        scout_match_button.setOnClickListener(this)

        super.onViewCreated(view, savedInstanceState)
    }


    fun toggleServer(state: Boolean) {
        if (getDefaultBluetoothAdapter().isEnabled) {
            viewModel.changeServerStatus(state)
        } else {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_BT_ENABLE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_BT_ENABLE && resultCode == RESULT_OK) {
            toggleServer(true)
        }
    }
}


class ServerFragmentViewModel(application: Application) : AndroidViewModel(application), LifecycleObserver {
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

    fun changeServerStatus(status: Boolean) {
        val service = serviceConnection.getService()
        service?.changeServerStatus(status)
    }

    fun onServerStatusChanged(func: (ServerStatus) -> Unit) {
        subscriptions.add(serviceConnection.getService()!!.toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(func))
    }

    override fun onCleared() {
        subscriptions.unsubscribe()
    }
}
