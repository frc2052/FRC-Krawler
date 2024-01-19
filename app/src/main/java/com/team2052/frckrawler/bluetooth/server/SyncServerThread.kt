package com.team2052.frckrawler.bluetooth.server

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.team2052.frckrawler.bluetooth.BluetoothSyncConstants
import com.team2052.frckrawler.bluetooth.bufferedIO
import com.team2052.frckrawler.bluetooth.di.SyncEntryPoint
import dagger.hilt.EntryPoints
import timber.log.Timber

@SuppressLint("MissingPermission")
class SyncServerThread(
  context: Context,
  private val gameId: Int,
  private val eventId: Int,
) : Thread("frckrawler-sync-server") {
  companion object {
    private const val LOG_TAG = "SyncServer"
  }

  private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

  private val entryPoint = EntryPoints.get(context.applicationContext, SyncEntryPoint::class.java)
  private val opFactory = entryPoint.syncOperationFactory()
  private val scoutObserver = entryPoint.connectedScoutObserver()

  private var serverSocket: BluetoothServerSocket? = null

  override fun run() {
    Timber.d("Opening server")

    // TODO handle interrupt?
    while (serverSocket == null) {
      // TODO catch?
      serverSocket = bluetoothManager.adapter.listenUsingRfcommWithServiceRecord(
        BluetoothSyncConstants.ServiceName,
        BluetoothSyncConstants.Uuid
      )
    }

    // TODO handle interrupt?
    // TODO log to a database for troubleshooting?
    while (true) {
      Timber.d("Still running")
      val clientSocket = serverSocket!!.accept()
      val clientDevice = clientSocket.remoteDevice
      Timber.d("Client connected: ${clientDevice.name}")
      syncWithClient(clientSocket)
    }
  }

  // TODO catch exceptions, handle errors
  private fun syncWithClient(clientSocket: BluetoothSocket) {
    clientSocket.bufferedIO { output, input ->
      val operations = opFactory.createServerOperations(gameId = gameId, eventId = eventId)
      operations.forEach { op ->
        Timber.d("Sync operation ${op.javaClass.simpleName} starting")
        val result = op.execute(output, input)
        Timber.d("Sync operation ${op.javaClass.simpleName} result: $result")
      }
    }

    val device = clientSocket.remoteDevice
    scoutObserver.notifyScoutSynced(device.name, device.address)
  }

  // TODO handle closing sockets?
}