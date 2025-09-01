package com.team2052.frckrawler.bluetooth.server

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.team2052.frckrawler.bluetooth.BluetoothSyncConstants
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.bufferedIO
import com.team2052.frckrawler.bluetooth.di.SyncEntryPoint
import dagger.hilt.EntryPoints
import timber.log.Timber

@SuppressLint("MissingPermission")
class SyncServerThread(
  context: Context,
  private val gameId: Int,
  private val eventId: Int,
  private val statusProvider: ServerStatusProvider,
) : Thread("frckrawler-sync-server") {

  private val bluetoothManager: BluetoothManager =
    context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

  private val entryPoint = EntryPoints.get(context.applicationContext, SyncEntryPoint::class.java)
  private val opFactory = entryPoint.syncOperationFactory()
  private val scoutObserver = entryPoint.connectedScoutObserver()

  private var serverSocket: BluetoothServerSocket? = null

  override fun run() {
    Timber.d("Opening server")

    while (serverSocket == null && !isInterrupted) {
      serverSocket = bluetoothManager.adapter.listenUsingRfcommWithServiceRecord(
        BluetoothSyncConstants.ServiceName,
        BluetoothSyncConstants.Uuid
      )
    }

    statusProvider.notifyServerStarted()

    while (!interrupted()) {
      try {
        val clientSocket = serverSocket!!.accept()
        val clientDevice = clientSocket.remoteDevice
        Timber.d("Client connected: ${clientDevice.name}")

        syncWithClient(clientSocket)
      } catch (e: Exception) {
        Timber.w(e, "Failed to connect client")
        break

      }
    }

    statusProvider.notifyServerStopped()
    serverSocket?.close()
  }

  private fun syncWithClient(clientSocket: BluetoothSocket) {
    var syncSucceeded = true
    clientSocket.bufferedIO { output, input ->
      val operations = opFactory.createServerOperations(gameId = gameId, eventId = eventId)
      operations.forEach { op ->
        Timber.d("Sync operation ${op.javaClass.simpleName} starting")
        try {
          val result = op.execute(output, input)
          if (result != OperationResult.Success) {
            syncSucceeded = false
            Timber.e("Sync operation ${op.javaClass.simpleName} failed: $result")
          } else {
            Timber.d("Sync operation ${op.javaClass.simpleName} completed successfully")
          }
        } catch (e: Exception) {
          syncSucceeded = false
          Timber.e(e, "Sync operation ${op.javaClass.simpleName} failed")
        }
      }
    }

    val device = clientSocket.remoteDevice
    scoutObserver.notifyScoutSynced(device.name, device.address, syncSucceeded)

    clientSocket.close()
  }

}