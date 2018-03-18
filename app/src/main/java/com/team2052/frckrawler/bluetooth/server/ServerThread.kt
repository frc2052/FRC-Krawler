package com.team2052.frckrawler.bluetooth.server

import android.bluetooth.BluetoothServerSocket
import android.content.Context
import android.util.Log
import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.BluetoothConstants
import com.team2052.frckrawler.bluetooth.syncable.ScoutDataSyncable
import com.team2052.frckrawler.bluetooth.syncable.ScoutWrongVersionSyncable
import com.team2052.frckrawler.data.RxDBManager
import com.team2052.frckrawler.helpers.getDefaultBluetoothAdapterOrNull
import com.team2052.frckrawler.models.ServerLogEntry
import rx.Observer
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

class ServerThread(private val statusObserver: Observer<ServerStatus>, private val context: Context) : Thread() {
    var isOpen: Boolean = true
    private var currentSyncDeviceName = ""
    private var mRxDbManager: RxDBManager = RxDBManager.getInstance(context)
    private var serverSocket: BluetoothServerSocket? = null

    override fun run() {
        Log.d(TAG, "Server Open")
        isOpen = true
        while (isOpen) {
            statusObserver.onNext(ServerStatus(isOpen, false, null))

            try {
                serverSocket = getDefaultBluetoothAdapterOrNull()?.listenUsingRfcommWithServiceRecord(BluetoothConstants.SERVICE_NAME, UUID.fromString(BluetoothConstants.UUID))
                val clientSocket = serverSocket?.accept()
                serverSocket?.close()
                if (clientSocket != null) {
                    Log.d(TAG, "Starting sync")
                    statusObserver.onNext(ServerStatus(isOpen, true, clientSocket.remoteDevice))

                    currentSyncDeviceName = clientSocket.remoteDevice.name

                    val toScoutStream = ObjectOutputStream(clientSocket.outputStream)
                    val fromScoutStream = ObjectInputStream(clientSocket.inputStream)

                    val serverBluetoothConnection = ServerBluetoothConnection(clientSocket, fromScoutStream, toScoutStream)
                    handleServerBluetoothConnection(serverBluetoothConnection)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                statusObserver.onNext(ServerStatus(state = true))
                closeServer()
            }
        }
        closeServer()
        Log.d(TAG, "Server Closed")
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun handleServerBluetoothConnection(connection: ServerBluetoothConnection) {
        if (!connection.isMatchVersionCode) {
            connection.sendScoutSyncable(ScoutWrongVersionSyncable())
            insertLog(String.format(
                    "ERROR: %s did not meet server's version requirements the server is running version code #%d and %s is running version code %d", currentSyncDeviceName,
                    BuildConfig.VERSION_CODE,
                    currentSyncDeviceName,
                    connection.scoutVersion
            ))
            return
        }

        //Hash
        /*if (!Strings.isNullOrEmpty(connection.serverSyncable!!.event_hash)) {
            val count = mRxDbManager.eventsTable.queryBuilder.where(EventDao.Properties.Unique_hash.eq(connection.serverSyncable!!.event_hash)).count()
            Log.d(TAG, count.toString() + " event(s) found with hash")

            if (count.equals(0)) {
                connection.sendScoutSyncable(ScoutEventMatchSyncable())
                insertLog(String.format("ERROR: Device named %s that is currently synced event did not match this device's unique event id", currentSyncDeviceName))
                return
            }
        }*/

        when (connection.serverSyncable!!.sync_code) {
            BluetoothConstants.SCOUT_SYNC -> {
                connection.sendScoutSyncable(ScoutDataSyncable(context))
                insertLog(String.format("INFO: Successfully synced with %s", currentSyncDeviceName))
                statusObserver.onNext(ServerStatus(true, false, null))
            }
        }

        connection.serverSyncable?.saveToServer(context)
    }


    private fun insertLog(message: String) {
        Log.i(TAG, message)
        mRxDbManager.serverLogEntries.insert(ServerLogEntry(Date(), message))
    }

    fun closeServer() {
        isOpen = false
        try {
            serverSocket?.close()
        } catch (ignored: IOException) {
        }
        statusObserver.onNext(ServerStatus.off)
    }

    companion object {
        var TAG = ServerThread::class.java.simpleName
    }
}
