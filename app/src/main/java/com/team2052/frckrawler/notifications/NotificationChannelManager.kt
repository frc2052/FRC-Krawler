package com.team2052.frckrawler.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NotificationChannelManager @Inject constructor(
  @ApplicationContext private val context: Context
) {

  private val notificationManager = NotificationManagerCompat.from(context)

  fun ensureChannelsCreated() {
    FrcKrawlerNotificationChannel.entries.forEach { channel ->
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
}