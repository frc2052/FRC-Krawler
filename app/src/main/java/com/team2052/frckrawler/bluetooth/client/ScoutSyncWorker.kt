package com.team2052.frckrawler.bluetooth.client

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.team2052.frckrawler.R
import com.team2052.frckrawler.bluetooth.BluetoothSyncConstants
import com.team2052.frckrawler.bluetooth.SyncOperationFactory
import com.team2052.frckrawler.bluetooth.bufferedIO
import com.team2052.frckrawler.notifications.FrcKrawlerNotificationChannel
import com.team2052.frckrawler.notifications.NotificationChannelManager
import com.team2052.frckrawler.notifications.NotificationId
import com.team2052.frckrawler.ui.MainActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.time.Instant
import java.util.Optional

@HiltWorker
class ScoutSyncWorker @AssistedInject constructor(
  @Assisted appContext: Context,
  @Assisted workerParams: WorkerParameters,
  bluetoothAdapterOptional: Optional<BluetoothAdapter>,
  private val opFactory: SyncOperationFactory,
  private val notificationChannelManager: NotificationChannelManager
) : CoroutineWorker(appContext, workerParams) {

  private val bluetoothAdapter = bluetoothAdapterOptional.get()

  companion object {
    const val DATA_SERVER_ADDRESS = "server_address"
    const val RESULT_END_TIMESTAMP = "end_timestamp"
  }

  @SuppressLint("MissingPermission")
  override suspend fun doWork(): Result {
    Timber.tag("client").d("starting sync")
    notificationChannelManager.ensureChannelsCreated()
    setForeground(getForegroundInfo())

    val serverAddress = inputData.getString(DATA_SERVER_ADDRESS)
    val serverDevice = bluetoothAdapter.getRemoteDevice(serverAddress)

    val connection =
      serverDevice.createInsecureRfcommSocketToServiceRecord(BluetoothSyncConstants.Uuid)

    try {
      connection.connect()
      connection.use { socket ->
        socket.bufferedIO { output, input ->
          val operations = opFactory.createScoutOperations()
          operations.forEach { op ->
            Timber.d("Sync operation ${op.javaClass.simpleName} starting")
            try {
              val result = op.execute(output, input)
              Timber.d("Sync operation ${op.javaClass.simpleName} result: $result")
            } catch (e: Exception) {
              Timber.w(e, "Sync operation ${op.javaClass.simpleName} failed")
              return Result.failure()
            }
          }
        }
      }
    } catch (e: Exception) {
      Timber.w(e, "Failed to connect to server")
      return Result.failure()
    }

    val data = Data.Builder()
      .putLong(RESULT_END_TIMESTAMP, Instant.now().epochSecond)
      .build()

    return Result.success(data)
  }

  override suspend fun getForegroundInfo(): ForegroundInfo {
    val pendingIntent: PendingIntent =
      Intent(applicationContext, MainActivity::class.java).let { notificationIntent ->
        PendingIntent.getActivity(
          applicationContext, 0, notificationIntent,
          PendingIntent.FLAG_IMMUTABLE
        )
      }
    val title = applicationContext.getText(R.string.scout_sync_notification_title)
    val notification =
      NotificationCompat.Builder(applicationContext, FrcKrawlerNotificationChannel.Sync.id)
        .setContentTitle(title)
        .setSmallIcon(R.drawable.ic_logo)
        .setContentIntent(pendingIntent)
        .build()

    val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
    } else 0

    return ForegroundInfo(
      NotificationId.ScoutSyncNotification,
      notification,
      serviceType
    )
  }
}