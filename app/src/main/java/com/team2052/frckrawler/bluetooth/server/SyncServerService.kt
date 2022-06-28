package com.team2052.frckrawler.bluetooth.server

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.team2052.frckrawler.R
import com.team2052.frckrawler.notifications.FrcKrawlerNotificationChannel
import com.team2052.frckrawler.notifications.NotificationChannelManager
import com.team2052.frckrawler.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Service that acts as a server for syncing with scouts. This server runs as a foreground service
 * to allow syncing even when the app is not in the foreground.
 *
 * TODO add an action to the notification to stop the server?
 */
@AndroidEntryPoint
class SyncServerService : Service() {

  companion object {
    private const val foregroundNotificationId = 2052

    internal val ACTION_START = "com.team2052.frckrawler.bluetooth.server.START"
    internal val ACTION_STOP = "com.team2052.frckrawler.bluetooth.server.STOP"
  }

  @Inject lateinit internal var notificationChannelManager: NotificationChannelManager

  lateinit var serverThread: SyncServerThread

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // We want to be able to finish syncing if the app goes in the background, so
    // we run in the foreground
    notificationChannelManager.ensureChannelsCreated()
    startForeground(foregroundNotificationId, getForegroundNotification())
    startServer()

    return START_REDELIVER_INTENT
  }

  override fun onDestroy() {
    super.onDestroy()
  }

  private fun getForegroundNotification(): Notification {
    // TODO format with actual number of connected clients
    val notificationText = resources.getQuantityString(R.plurals.server_sync_clients_connected, 0, 0)

    // TODO deep link to server screen
    val pendingIntent: PendingIntent =
      Intent(this, MainActivity::class.java).let { notificationIntent ->
        PendingIntent.getActivity(this, 0, notificationIntent,
          PendingIntent.FLAG_IMMUTABLE)
      }

    return Notification.Builder(this, FrcKrawlerNotificationChannel.Sync.id)
      .setContentTitle(getText(R.string.server_sync_notification_title))
      .setContentText(notificationText)
      .setSmallIcon(R.drawable.ic_logo)
      .setContentIntent(pendingIntent)
      .build()
  }

  private fun startServer() {
    serverThread = SyncServerThread(this)
    serverThread.start()
  }

  private fun stopServer() {
    serverThread.interrupt()
  }
}