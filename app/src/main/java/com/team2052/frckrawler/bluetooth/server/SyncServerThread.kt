package com.team2052.frckrawler.bluetooth.server

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.team2052.frckrawler.bluetooth.BluetoothSyncConstants
import com.team2052.frckrawler.bluetooth.OperationResult
import com.team2052.frckrawler.bluetooth.SyncOperationFactory
import com.team2052.frckrawler.bluetooth.bufferedIO
import com.team2052.frckrawler.ui.server.home.ServerState
import dev.zacsweers.metro.Inject
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

  @Inject private lateinit var opFactory: SyncOperationFactory
  @Inject private lateinit var scoutObserver: ConnectedScoutObserver

  private var serverSocket: BluetoothServerSocket? = null

  override fun run() {
    Timber.i("Opening server")

    while (serverSocket == null && !isInterrupted) {
      serverSocket = bluetoothManager.adapter.listenUsingRfcommWithServiceRecord(
        BluetoothSyncConstants.ServiceName,
        BluetoothSyncConstants.Uuid
      )
    }

    statusProvider.setState(
      ServerState.Enabled(gameId = gameId, eventId = eventId)
    )

    while (!interrupted()) {
      try {
        val clientSocket = serverSocket!!.accept()
        val clientDevice = clientSocket.remoteDevice
        Timber.i("Client connected: ${clientDevice.name}")

        syncWithClient(clientSocket)
      } catch (e: Exception) {
        Timber.w(e, "Failed to connect client")
        break

      }
    }

    serverSocket?.close()
    statusProvider.setState(ServerState.Disabled)
  }

  private fun syncWithClient(clientSocket: BluetoothSocket) {
    var syncSucceeded = true
    clientSocket.bufferedIO { output, input ->
      val operations = opFactory.createServerOperations(gameId = gameId, eventId = eventId)
      operations.forEach { op ->
        Timber.i("Sync operation ${op.javaClass.simpleName} starting")
        try {
          val result = op.execute(output, input)
          if (result != OperationResult.Success) {
            syncSucceeded = false
            Timber.e("Sync operation ${op.javaClass.simpleName} failed: $result")
          } else {
            Timber.i("Sync operation ${op.javaClass.simpleName} completed successfully")
          }
        } catch (e: Exception) {
          syncSucceeded = false
          Timber.e(e, "Sync operation ${op.javaClass.simpleName} failed fatally")
          return@bufferedIO
        }
      }
    }

    val device = clientSocket.remoteDevice
    scoutObserver.notifyScoutSynced(device.name, device.address, syncSucceeded)

    clientSocket.close()
  }

}