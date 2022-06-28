package com.team2052.frckrawler.notifications

import android.app.NotificationChannel
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationChannelManager @Inject constructor(
  @ApplicationContext private val context: Context
) {

  private val notificationManager = NotificationManagerCompat.from(context)

  fun ensureChannelsCreated() {
    FrcKrawlerNotificationChannel.values().forEach { channel ->
      notificationManager.createNotificationChannel(
        NotificationChannel(
          channel.id,
          context.getText(channel.nameRes),
          NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
      )
    }
  }
}