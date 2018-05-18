package com.team2052.frckrawler.services

import android.app.IntentService
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.core.bluetooth.BluetoothConnection
import com.team2052.frckrawler.core.bluetooth.BluetoothConstants
import com.team2052.frckrawler.core.bluetooth.BluetoothHelper
import com.team2052.frckrawler.core.bluetooth.StartBluetoothConnectionEvent
import com.team2052.frckrawler.core.bluetooth.scout.events.ScoutSyncErrorEvent
import com.team2052.frckrawler.core.bluetooth.scout.events.ScoutSyncStartEvent
import com.team2052.frckrawler.core.bluetooth.scout.events.ScoutSyncSuccessEvent
import com.team2052.frckrawler.core.bluetooth.syncable.ScoutSyncable
import com.team2052.frckrawler.core.bluetooth.syncable.ServerDataSyncable
import com.team2052.frckrawler.core.data.models.RxDBManager
import org.greenrobot.eventbus.EventBus
import rx.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

class StartSyncIntentService : IntentService("StartSyncIntentService") {
    //TODO make events less annoying

    override fun onHandleIntent(intent: Intent?) {
        EventBus.getDefault().post(ScoutSyncStartEvent())
        if (intent != null) {
            val macAddress = intent.getStringExtra(MAC_ADDRESS_KEY)
            val device: BluetoothDevice = BluetoothHelper.getDevice(macAddress) ?: return
            AndroidSchedulers.mainThread().createWorker().schedule { EventBus.getDefault().post(ScoutSyncStartEvent()) }

            var socket: BluetoothSocket? = null
            var outputStream: ObjectOutputStream? = null
            var inputStream: ObjectInputStream? = null


            AndroidSchedulers.mainThread().createWorker().schedule { EventBus.getDefault().post(StartBluetoothConnectionEvent()) }
            try {
                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothConstants.UUID))
                socket.connect()

                outputStream = ObjectOutputStream(socket.outputStream)
                inputStream = ObjectInputStream(socket.inputStream)
            } catch (e: IOException) {
            }

            val bluetoothConnection = BluetoothConnection(socket, inputStream, outputStream)
            val outputStreamWrapper = bluetoothConnection.outputStreamWrapper

            try {
                outputStreamWrapper
                        .writeInteger(BuildConfig.VERSION_CODE)
                        .writeObject(ServerDataSyncable(applicationContext))
                        .send()
            } catch (e: IOException) {
            }

            val scoutSyncable: ScoutSyncable = bluetoothConnection.inputStream.readObject() as ScoutSyncable
            bluetoothConnection.closeConnection()

            val code = scoutSyncable.returnCode
            when (code) {
                BluetoothConstants.ReturnCodes.OK -> {
                    // Knowing that the data was sent to the server, we can now delete the data on our device, and sync with the server
                    RxDBManager.getInstance(applicationContext).runInTx(Runnable { RxDBManager.getInstance(applicationContext).deleteAll() })
                    scoutSyncable.saveToScout(applicationContext)
                    com.team2052.frckrawler.core.common.ScoutHelper.setDeviceAsScout(applicationContext, true)
                    com.team2052.frckrawler.core.common.ScoutHelper.setSyncDevice(applicationContext, device)
                    AndroidSchedulers.mainThread().createWorker().schedule { EventBus.getDefault().post(ScoutSyncSuccessEvent()) }
                }
                BluetoothConstants.ReturnCodes.VERSION_ERROR -> AndroidSchedulers.mainThread().createWorker().schedule { ScoutSyncErrorEvent("The server version is incompatible with your version") }
                BluetoothConstants.ReturnCodes.EVENT_MATCH_ERROR -> {
                    RxDBManager.getInstance(applicationContext).runInTx(Runnable { RxDBManager.getInstance(applicationContext).deleteAll() })
                    AndroidSchedulers.mainThread().createWorker().schedule { ScoutSyncErrorEvent("This device's data did not match up with the server tablet. The data from this tablet was lost.") }
                }
                else -> AndroidSchedulers.mainThread().createWorker().schedule { ScoutSyncErrorEvent("Scout got response code that this device couldn't handle. Try updating") }
            }
        }
    }

    companion object {
        val MAC_ADDRESS_KEY = "MAC_ADDRESS"
    }
}