package com.team2052.frckrawler.ui.event.add

import com.team2052.frckrawler.data.remote.model.TbaSimpleEvent
import java.time.Year

data class AddEventScreenState(
    val years: List<Int>,
    val events: List<TbaSimpleEvent> = emptyList(),
    val hasNetworkError: Boolean = false
)