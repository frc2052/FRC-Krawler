package com.team2052.frckrawler.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.annotation.StringRes
import com.team2052.frckrawler.R

enum class FrcKrawlerNotificationChannel(
  @StringRes val nameRes: Int,
  @StringRes val descriptionRes: Int,
  val id: String
) {
  Sync(
    nameRes = R.string.notification_channel_sync_name,
    descriptionRes = R.string.notification_channel_sync_description,
    id = "sync"
  )
}