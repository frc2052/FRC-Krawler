package com.team2052.frckrawler.ui.server.home

/**
 * State of the server
 */
sealed class ServerState {
  data class Enabled(
    val gameId: Int,
    val eventId: Int,
  ): ServerState()
  data object Enabling: ServerState()
  data object Disabled: ServerState()
  data object Disabling: ServerState()
}