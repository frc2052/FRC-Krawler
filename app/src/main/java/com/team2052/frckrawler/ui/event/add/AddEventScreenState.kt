package com.team2052.frckrawler.ui.event.add

import com.team2052.frckrawler.data.remote.model.TbaSimpleEvent

data class AddEventScreenState(
  val years: List<Int>,
  val events: List<TbaSimpleEvent> = emptyList(),
  val areEventsLoading: Boolean = false,
  val hasNetworkError: Boolean = false,
  val isSavingEvent: Boolean = false,
)