@file:JvmName("BluetoothUtil")

package com.team2052.frckrawler.helpers

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import com.team2052.frckrawler.BuildConfig
import com.team2052.frckrawler.bluetooth.BluetoothConnection
import com.team2052.frckrawler.bluetooth.BluetoothConstants
import com.team2052.frckrawler.bluetooth.StartBluetoothConnectionEvent
import com.team2052.frckrawler.bluetooth.scout.ScoutSyncStatus
import com.team2052.frckrawler.bluetooth.scout.events.ScoutSyncStartEvent
import com.team2052.frckrawler.bluetooth.syncable.ScoutSyncable
import com.team2052.frckrawler.bluetooth.syncable.ServerDataSyncable
import com.team2052.frckrawler.data.RxDBManager
import org.greenrobot.eventbus.EventBus
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.*

fun getDefaultBluetoothAdapterOrNull(): BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter();
fun getDefaultBluetoothAdapter(): BluetoothAdapter = getDefaultBluetoothAdapterOrNull()!!

inline fun getDefaultBluetoothAdapterOrNull(context: Context, noDevice: Context.() -> Unit): BluetoothAdapter? {
    val bluetoothAdapter = getDefaultBluetoothAdapterOrNull()
    if (bluetoothAdapter == null) context.noDevice()
    return bluetoothAdapter
}

inline fun getDefaultBluetoothAdapterOrNull(present: () -> Unit, noDevice: () -> Unit) {
    val bluetoothAdapter = getDefaultBluetoothAdapterOrNull()
    if (bluetoothAdapter == null) noDevice() else present()
}

fun Set<BluetoothDevice>.getBluetoothDeviceNames(): Array<CharSequence> {
    val names = mutableListOf<CharSequence>()
    this.forEach { it -> names.add(it.name) }
    return names.toTypedArray()
}

fun getBluetoothDevices(): Set<BluetoothDevice> = getDefaultBluetoothAdapter().bondedDevices


fun BluetoothDevice?.getBluetoothConnectionObservable(): Observable<BluetoothConnection> = Observable.just<BluetoothDevice>(this)
        .map { bluetoothDevice ->
            AndroidSchedulers.mainThread().createWorker().schedule { EventBus.getDefault().post(StartBluetoothConnectionEvent()) }

            val socket: BluetoothSocket
            val outputStream: ObjectOutputStream
            val inputStream: ObjectInputStream

            try {
                socket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothConstants.UUID))
                socket.connect()
                outputStream = ObjectOutputStream(socket.outputStream)
                inputStream = ObjectInputStream(socket.inputStream)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

            BluetoothConnection(socket, inputStream, outputStream)
        }


fun getScoutSyncObservable(device: BluetoothDevice, context: Context): Observable<ScoutSyncStatus> {
    return device.getBluetoothConnectionObservable()
            .map { bluetoothConnection ->
                AndroidSchedulers.mainThread().createWorker().schedule({ EventBus.getDefault().post(ScoutSyncStartEvent()) })
                val outputStreamWrapper = bluetoothConnection.outputStreamWrapper
                try {
                    outputStreamWrapper
                            .writeInteger(BuildConfig.VERSION_CODE)
                            .writeObject(ServerDataSyncable(context))
                            .send()
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }

                bluetoothConnection
            }
            .map { bluetoothConnection ->
                val scoutSyncable: ScoutSyncable = bluetoothConnection.inputStream.readObject() as ScoutSyncable
                bluetoothConnection.closeConnection()
                val code = scoutSyncable.returnCode

                when (code) {
                    BluetoothConstants.ReturnCodes.OK -> {
                        // Knowing that the data was sent to the server, we can now delete the data on our device, and sync with the server
                        RxDBManager.getInstance(context).runInTx(Runnable { RxDBManager.getInstance(context).deleteAll() })
                        scoutSyncable.saveToScout(context)

                        ScoutHelper.setDeviceAsScout(context, true)
                        ScoutHelper.setSyncDevice(context, device)
                        ScoutSyncStatus(true)
                    }
                    BluetoothConstants.ReturnCodes.EVENT_MATCH_ERROR -> ScoutSyncStatus(false, "The server version is incompatible with your version")
                    BluetoothConstants.ReturnCodes.VERSION_ERROR -> {
                        RxDBManager.getInstance(context).runInTx(Runnable { RxDBManager.getInstance(context).deleteAll() })
                        ScoutSyncStatus(false, "This device's data did not match up with the server device. The data from this tablet was lost.")
                    }
                    else -> ScoutSyncStatus(false, "Scout got response code that this device couldn't handle. Try updating")
                }
            }
}