package com.team2052.frckrawler.ui.scout

sealed class ServerConnectionState {
  data class Connected(val name: String): ServerConnectionState()
  object NotConnected: ServerConnectionState()
  object Connecting: ServerConnectionState()
  object NoFrcKrawlerServiceFound: ServerConnectionState()
  object PairingFailed: ServerConnectionState()
}