package com.team2052.frckrawler.bluetooth.server

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SyncServiceController @Inject constructor(
  @ApplicationContext private val context: Context
){
  fun startServer() {
    context.startService(Intent(context, SyncServerService::class.java))
  }

  fun stopServer() {
    context.stopService(Intent(context, SyncServerService::class.java))
  }
}