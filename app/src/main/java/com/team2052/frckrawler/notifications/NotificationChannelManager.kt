package com.team2052.frckrawler.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import com.team2052.frckrawler.di.ApplicationContext
import dev.zacsweers.metro.Inject

@Inject
class NotificationChannelManager(
  @ApplicationContext private val context: Context
) {

  private val notificationManager = NotificationManagerCompat.from(context)

  fun ensureChannelsCreated() {
    FrcKrawlerNotificationChannel.entries.forEach { channel ->
      notificationManager.createNotificationChannel(
        NotificationChannel(
          channel.id,
          context.getText(channel.nameRes),
          NotificationManager.IMPORTANCE_DEFAULT
        )
      )
    }
  }
}