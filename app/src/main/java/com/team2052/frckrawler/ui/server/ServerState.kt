package com.team2052.frckrawler.ui.server

sealed class ServerState {
    object Running: ServerState()
    object Stopped: ServerState()
}
