package com.team2052.frckrawler.bluetooth.server

import android.content.Context
import android.content.Intent
import com.team2052.frckrawler.di.ApplicationContext
import dev.zacsweers.metro.Inject

@Inject
class SyncServiceController(
  @ApplicationContext private val context: Context
) {
  fun startServer(
    gameId: Int,
    eventId: Int
  ) {
    val intent = Intent(context, SyncServerService::class.java).apply {
      putExtra(SyncServerService.EXTRA_GAME_ID, gameId)
      putExtra(SyncServerService.EXTRA_EVENT_ID, eventId)
    }
    context.startService(intent)
  }

  fun stopServer() {
    context.stopService(Intent(context, SyncServerService::class.java))
  }
}