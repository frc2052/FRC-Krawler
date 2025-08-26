package com.team2052.frckrawler.bluetooth.server

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.res.TypedArrayUtils.getText
import com.team2052.frckrawler.R
import com.team2052.frckrawler.notifications.FrcKrawlerNotificationChannel
import com.team2052.frckrawler.notifications.NotificationChannelManager
import com.team2052.frckrawler.notifications.NotificationId
import com.team2052.frckrawler.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Service that acts as a server for syncing with scouts. This server runs as a foreground service
 * to allow syncing even when the app is not in the foreground.
 *
 * TODO request notification permission
 */
@AndroidEntryPoint
class SyncServerService : Service() {

  companion object {
    internal const val EXTRA_GAME_ID = "game_id"
    internal const val EXTRA_EVENT_ID = "event_id"
  }

  @Inject
  internal lateinit var notificationChannelManager: NotificationChannelManager

  @Inject
  internal lateinit var statusProvider: ServerStatusProvider

  private lateinit var serverThread: SyncServerThread

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // We want to be able to finish syncing if the app goes in the background, so
    // we run in the foreground
    notificationChannelManager.ensureChannelsCreated()

    val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
    } else 0

    ServiceCompat.startForeground(
      this,
      NotificationId.ServerServiceNotification,
      getForegroundNotification(),
      serviceType)

    val extras = intent?.extras
      ?: throw IllegalStateException("SyncServerService request game and event ID extras")
    val gameId = extras.getInt(EXTRA_GAME_ID)
    val eventId = extras.getInt(EXTRA_EVENT_ID)
    startServer(gameId, eventId)

    return START_REDELIVER_INTENT
  }

  override fun onDestroy() {
    super.onDestroy()
    stopServer()
  }

  private fun getForegroundNotification(): Notification {
    // TODO deep link to server screen
    val pendingIntent: PendingIntent =
      Intent(this, MainActivity::class.java).let { notificationIntent ->
        PendingIntent.getActivity(
          this, 0, notificationIntent,
          PendingIntent.FLAG_IMMUTABLE
        )
      }

    return NotificationCompat.Builder(this, FrcKrawlerNotificationChannel.Sync.id)
      .setContentTitle(getText(R.string.server_sync_notification_title))
      .setSmallIcon(R.drawable.ic_logo)
      .setContentIntent(pendingIntent)
      .build()
  }

  private fun startServer(
    gameId: Int,
    eventId: Int,
  ) {
    serverThread = SyncServerThread(this, gameId, eventId, statusProvider)
    serverThread.start()
  }

  private fun stopServer() {
    serverThread.interrupt()
  }
}