package com.team2052.frckrawler.bluetooth.server

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import com.team2052.frckrawler.FRCKrawlerApp
import com.team2052.frckrawler.R
import com.team2052.frckrawler.links.Deeplinks
import com.team2052.frckrawler.notifications.FrcKrawlerNotificationChannel
import com.team2052.frckrawler.notifications.NotificationChannelManager
import com.team2052.frckrawler.notifications.NotificationId
import com.team2052.frckrawler.ui.MainActivity
import com.team2052.frckrawler.ui.server.home.ServerState
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.MembersInjector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Service that acts as a server for syncing with scouts. This server runs as a foreground service
 * to allow syncing even when the app is not in the foreground.
 */
class SyncServerService : Service() {

  companion object {
    internal const val EXTRA_GAME_ID = "game_id"
    internal const val EXTRA_EVENT_ID = "event_id"
  }

  @Inject private lateinit var notificationChannelManager: NotificationChannelManager
  @Inject private lateinit var statusProvider: ServerStatusProvider
  @Inject private lateinit var connectedScoutObserver: ConnectedScoutObserver
  @Inject private lateinit var threadInjector: MembersInjector<SyncServerThread>

  private lateinit var serverThread: SyncServerThread

  private val scope = CoroutineScope(Dispatchers.Main)

  override fun onBind(intent: Intent): IBinder? {
    return null
  }

  override fun onCreate() {
    super.onCreate()

    // TODO use metrox-android when we go to minSDk 28
    (application as FRCKrawlerApp).appGraph.inject(this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    // We want to be able to finish syncing if the app goes in the background, so
    // we run in the foreground
    notificationChannelManager.ensureChannelsCreated()

    val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
      ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
    } else 0

    val extras = intent?.extras
      ?: throw IllegalStateException("SyncServerService requires game and event ID extras")
    val gameId = extras.getInt(EXTRA_GAME_ID)
    val eventId = extras.getInt(EXTRA_EVENT_ID)
    startServer(gameId, eventId)

    ServiceCompat.startForeground(
      this,
      NotificationId.ServerServiceNotification,
      getForegroundNotification(
        connectedScouts = 0,
        gameId = gameId,
        eventId = eventId
      ),
      serviceType
    )

    val hasNotificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      val permission =
        ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
      permission == android.content.pm.PackageManager.PERMISSION_GRANTED
    } else true
    if (hasNotificationPermission) {
      scope.launch {
        connectedScoutObserver.devices.collectLatest { devices ->
          updateNotification(connectedScouts = devices.size, gameId = gameId, eventId = eventId)
        }
      }
    }

    return START_REDELIVER_INTENT
  }

  override fun onDestroy() {
    super.onDestroy()
    stopServer()
  }

  private fun updateNotification(
    connectedScouts: Int,
    gameId: Int,
    eventId: Int,
  ) {
    notificationChannelManager.ensureChannelsCreated()
    val notificationManager = getSystemService<NotificationManager>()
    notificationManager?.notify(
      NotificationId.ServerServiceNotification,
      getForegroundNotification(
        connectedScouts = connectedScouts,
        gameId = gameId,
        eventId = eventId
      )
    )
  }

  private fun getForegroundNotification(
    connectedScouts: Int,
    gameId: Int,
    eventId: Int,
  ): Notification {
    val notificationText =
      resources.getQuantityString(
        R.plurals.server_sync_clients_connected,
        connectedScouts,
        connectedScouts
      )

    val launchIntent: PendingIntent =
      Intent(this, MainActivity::class.java).let { notificationIntent ->
        notificationIntent.data = Deeplinks.ServerHome.buildUpon()
          .appendQueryParameter(Deeplinks.Params.GameId, gameId.toString())
          .appendQueryParameter(Deeplinks.Params.EventId, eventId.toString())
          .build()
        PendingIntent.getActivity(
          this, 0, notificationIntent,
          PendingIntent.FLAG_IMMUTABLE
        )
      }

    val stopIntent: PendingIntent =
      Intent(this, StopServerBroadcastReceiver::class.java).let { stopServiceIntent ->
        PendingIntent.getBroadcast(this, 0, stopServiceIntent, PendingIntent.FLAG_IMMUTABLE)
      }

    return NotificationCompat.Builder(this, FrcKrawlerNotificationChannel.Sync.id)
      .setContentTitle(getText(R.string.server_sync_notification_title))
      .setContentText(notificationText)
      .setSmallIcon(R.drawable.ic_logo)
      .setContentIntent(launchIntent)
      .addAction(
        R.drawable.ic_stop,
        getString(R.string.server_sync_notification_stop),
        stopIntent,
      )
      .build()
  }

  private fun startServer(
    gameId: Int,
    eventId: Int,
  ) {
    serverThread = SyncServerThread(this, gameId, eventId, statusProvider)
    threadInjector.injectMembers(serverThread)
    serverThread.start()
  }

  private fun stopServer() {
    scope.coroutineContext.cancelChildren()
    serverThread.interrupt()
    statusProvider.setState(ServerState.Disabled)
  }
}