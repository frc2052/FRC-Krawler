package com.team2052.frckrawler.bluetooth.server

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerStatusProvider @Inject constructor() {
  private val statusFlow = MutableStateFlow(false)

  fun notifyServerStarted() {
    statusFlow.value = true
  }

  fun notifyServerStopped() {
    statusFlow.value = false
  }

  /**
   * Emits true when the server is running, false if the server is not
   */
  fun getStatusFlow(): Flow<Boolean> = statusFlow
}