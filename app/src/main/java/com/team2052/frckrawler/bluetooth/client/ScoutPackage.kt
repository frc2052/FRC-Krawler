package com.team2052.frckrawler.bluetooth.client

import com.team2052.frckrawler.data.model.Event

/**
 * Data class containing all data sent to the server
 */
data class ScoutPackage(
    val event: Event
)
