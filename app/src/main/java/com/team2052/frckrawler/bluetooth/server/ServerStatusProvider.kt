package com.team2052.frckrawler.bluetooth.server

import com.team2052.frckrawler.ui.server.home.ServerState
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Inject
@SingleIn(AppScope::class)
class ServerStatusProvider {
  private val statusFlow = MutableStateFlow<ServerState>(ServerState.Disabled)

  fun setState(state: ServerState) {
    statusFlow.value = state
  }

  /**
   * Emits true when the server is running, false if the server is not
   */
  fun getStatusFlow(): Flow<ServerState> = statusFlow
}