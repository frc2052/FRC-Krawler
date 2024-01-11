package com.team2052.frckrawler.ui.server.home

import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.Game

data class ServerConfiguration(
    val event: Event?,
    val game: Game?
) {
    val isValid: Boolean
        get() = event != null && game != null
}