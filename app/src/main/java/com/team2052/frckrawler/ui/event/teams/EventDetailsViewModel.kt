package com.team2052.frckrawler.ui.event.teams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(EventDetailsViewModel::class)
@Inject
class EventDetailsViewModel(
  private val eventDao: EventDao,
  private val teamAtEventDao: TeamAtEventDao,
) : ViewModel() {
  private val _teams = MutableStateFlow<List<TeamAtEvent>>(emptyList())
  val teams: StateFlow<List<TeamAtEvent>> = _teams

  private val _event = MutableStateFlow<Event?>(null)
  val event: StateFlow<Event?> = _event

  fun loadEvent(eventId: Int) {
    viewModelScope.launch {
      _event.value = eventDao.get(eventId)
    }

    viewModelScope.launch {
      teamAtEventDao.getAllTeams(eventId).collect {
        _teams.value = it
      }
    }
  }

  fun deleteEvent() {
    viewModelScope.launch {
      _event.value?.let {
        eventDao.delete(it)
      }
    }
  }
}