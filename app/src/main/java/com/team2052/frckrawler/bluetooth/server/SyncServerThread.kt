package com.team2052.frckrawler.bluetooth.server

import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import androidx.core.content.PackageManagerCompat.LOG_TAG
import com.team2052.frckrawler.bluetooth.BluetoothSyncConstants
import com.team2052.frckrawler.bluetooth.bufferedIO
import com.team2052.frckrawler.bluetooth.di.SyncEntryPoint
import dagger.hilt.EntryPoints
import timber.log.Timber
import timber.log.Timber.Tree
import java.io.IOException

private const val SERVER_THREAD_TAG = "SyncServer"

class SyncServerThread(
  context: Context
) : Thread("frckrawler-sync-server") {
  private val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
  private val syncOperationFactory = EntryPoints.get(context.applicationContext, SyncEntryPoint::class.java).syncOperationFactory()

  private var serverSocket: BluetoothServerSocket? = null

  override fun run() {
    Timber.tag(SERVER_THREAD_TAG).d("Opening server")

    try {
      while (!isInterrupted) {
        serverSocket = bluetoothManager.adapter.listenUsingRfcommWithServiceRecord(
          BluetoothSyncConstants.ServiceName,
          BluetoothSyncConstants.Uuid
        )

        // TODO log to a database for troubleshooting?
        if (serverSocket != null) {
          Timber.tag(SERVER_THREAD_TAG).d("Waiting for client connection")

          // Blocks thread until a client connects
          // If the server thread is stopped during this operation an IOException is thrown
          try {
            val clientSocket = serverSocket!!.accept()
            val clientDevice = clientSocket.remoteDevice

            Timber.tag(SERVER_THREAD_TAG).d("Client connected: ${clientDevice.name}")

            syncWithClient(clientSocket)
          } catch(e: IOException) {
            Timber.tag(SERVER_THREAD_TAG).d(
              e,
              "Server socket closed before a client could connect",
            )
          }
        }
      }
    } catch (e: SecurityException) {
      // TODO Hoist event to ViewModel in order to request bluetooth enable
      Timber.tag(SERVER_THREAD_TAG).e(
        e,
        "Bluetooth permissions denied!",
      )
    } finally {
      interrupt()
    }
  }

  private fun syncWithClient(clientSocket: BluetoothSocket) {
    Timber.tag(SERVER_THREAD_TAG).d("Starting client sync")

    clientSocket.bufferedIO { output, input ->
      val operations = syncOperationFactory.createServerOperations()
      operations.forEach { op ->
        val result = op.execute(output, input)
        // TODO use operations results
        Timber.tag(SERVER_THREAD_TAG).d("Sync operation ${op.javaClass.simpleName} result: $result")
      }
    }
  }

  override fun interrupt() {
    try {
      serverSocket?.close()
    } catch (e: IOException) {
      Timber.tag(SERVER_THREAD_TAG).e(e, "Failed to close server socket!")
    }
  }
}