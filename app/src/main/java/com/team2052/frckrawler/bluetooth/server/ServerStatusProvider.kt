package com.team2052.frckrawler.bluetooth.server

import com.team2052.frckrawler.ui.server.home.ServerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ServerStatusProvider @Inject constructor() {
  private val statusFlow = MutableStateFlow<ServerState>(ServerState.Disabled)

  fun setState(state: ServerState) {
    statusFlow.value = state
  }

  /**
   * Emits true when the server is running, false if the server is not
   */
  fun getStatusFlow(): Flow<ServerState> = statusFlow
}