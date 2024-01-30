package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.bluetooth.BluetoothAvailabilityProvider
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.ui.components.GameAndEventState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModeSelectViewModel @Inject constructor(
  private val gameDao: GameDao,
  private val eventDao: EventDao,
  private val teamDao: TeamAtEventDao,
  bluetoothAvailabilityProvider: BluetoothAvailabilityProvider
) : ViewModel() {
  var serverConfigState = GameAndEventState()
  var localScoutConfigState = GameAndEventState()

  val bluetoothAvailability = bluetoothAvailabilityProvider.availability

  fun loadGamesAndEvents() {
    viewModelScope.launch {
      gameDao.getAll().collect {
        serverConfigState.availableGames = it
        localScoutConfigState.availableGames = it

        serverConfigState.updateSelectedGame()
        localScoutConfigState.updateSelectedGame()
      }
    }

    viewModelScope.launch {
      serverConfigState.updateEventsOnGameChange()
    }

    viewModelScope.launch {
      localScoutConfigState.updateEventsOnGameChange()
    }

    viewModelScope.launch {
      serverConfigState.updateTeamsOnEventChange()
    }

    viewModelScope.launch {
      localScoutConfigState.updateTeamsOnEventChange()
    }
  }

  private suspend fun GameAndEventState.updateEventsOnGameChange() {
    snapshotFlow { selectedGame }
      .flatMapLatest { game ->
        if (game != null) {
          eventDao.getAllForGame(game.id)
        } else {
          flowOf(emptyList())
        }
      }.collect { events ->
        availableEvents = events
        updateSelectedEvent()
      }
  }

  private suspend fun GameAndEventState.updateTeamsOnEventChange() {
    snapshotFlow { selectedEvent }
      .flatMapLatest { event ->
        if (event != null) {
          teamDao.getAllTeams(event.id)
        } else {
          flowOf(emptyList())
        }
      }.collect { teams ->
        hasTeams = teams.isNotEmpty()
      }
  }

  private fun GameAndEventState.updateSelectedGame() {
    val matchingGame = availableGames.firstOrNull { it.id == selectedGame?.id }
    selectedGame = matchingGame
  }

  private fun GameAndEventState.updateSelectedEvent() {
    val matchingEvent = availableEvents.firstOrNull { it.id == selectedEvent?.id }
    selectedEvent = matchingEvent
  }
}
