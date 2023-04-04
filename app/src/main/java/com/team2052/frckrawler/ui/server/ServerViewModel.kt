package com.team2052.frckrawler.ui.server

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.team2052.frckrawler.bluetooth.server.SyncServiceController
import com.team2052.frckrawler.ui.permissions.PermissionManager
import com.team2052.frckrawler.ui.permissions.RequiredPermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServerViewModel @Inject constructor(
    private val bluetoothAdapter: BluetoothAdapter,
    private val permissionManager: PermissionManager,
    private val syncServiceController: SyncServiceController
) : ViewModel() {
    var showPermissionRequests by mutableStateOf(false)
    var requestEnableBluetooth by mutableStateOf(false)

    var serverState: ServerState = ServerState.Stopped
//    by mutableStateOf(
//        if (syncServiceController.) {
//            ServerState.Running
//        } else {
//            ServerState.Stopped
//        }
//    )

    fun startServer() {
        // Check location strategy worked
        if (!permissionManager.hasPermissions(RequiredPermissions.serverPermissions)) {
            showPermissionRequests = true
            return
        }

        // Request bluetooth if not all ready enabled
        if (!bluetoothAdapter.isEnabled) {
            requestEnableBluetooth = true
            return
        }

        syncServiceController.startServer()
        serverState = ServerState.Running
    }

    fun stopServer() {
        syncServiceController.stopServer()
        serverState = ServerState.Stopped
    }

    fun connectScout(context: Context) {
        val filer = IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)

        context.registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
//                if (intent.getStringExtra(BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE)) {
//
//                }
            }
        }, filer)
    }
}