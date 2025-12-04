package com.team2052.frckrawler.bluetooth.server

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.team2052.frckrawler.FRCKrawlerApp
import dev.zacsweers.metro.Inject

/**
 * Listens for broadcasts (typically sent from a notification action) to stop the sync server
 */
class StopServerBroadcastReceiver : BroadcastReceiver() {

  @Inject private lateinit var controller: SyncServiceController
  override fun onReceive(context: Context, intent: Intent?) {
    (context.applicationContext as FRCKrawlerApp).appGraph.inject(this)
    controller.stopServer()
  }
}