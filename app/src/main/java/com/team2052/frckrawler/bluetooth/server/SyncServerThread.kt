package com.team2052.frckrawler.bluetooth.server

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.team2052.frckrawler.bluetooth.BluetoothSyncConstants
import com.team2052.frckrawler.bluetooth.SyncOperationFactory
import com.team2052.frckrawler.bluetooth.bufferedIO
import com.team2052.frckrawler.bluetooth.di.SyncEntryPoint
import dagger.hilt.EntryPoints
import okio.buffer
import okio.sink
import okio.source
import timber.log.Timber

class SyncServerThread(
  context: Context
) : Thread("frckrawler-sync-server") {
  companion object {
    private const val LOG_TAG = "SyncServer"
  }

  private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
  private val opFactory = EntryPoints.get(context.applicationContext, SyncEntryPoint::class.java).syncOperationFactory()

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
    while (true) {
      val clientSocket = serverSocket!!.accept()
      val clientDevice = clientSocket.remoteDevice
      Timber.d("Client connected: ${clientDevice.name}")
      // TODO perform sync

    }
  }

  private fun syncWithClient(clientSocket: BluetoothSocket) {
    clientSocket.bufferedIO { output, input ->
      val operations = opFactory.createServerOperations()
      operations.forEach { op ->
        val result = op.execute(output, input)
        // TODO handle result
      }
    }
  }

  // TODO handle closing sockets?
}