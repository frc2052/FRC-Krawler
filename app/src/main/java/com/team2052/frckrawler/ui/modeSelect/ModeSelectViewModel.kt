package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.bluetooth.BluetoothAvailabilityProvider
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.local.init.DatabaseInitializer
import com.team2052.frckrawler.data.local.prefs.FrcKrawlerPreferences
import com.team2052.frckrawler.ui.components.GameAndEventState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ModeSelectViewModel @Inject constructor(
  private val dbInitializer: DatabaseInitializer,
  private val gameDao: GameDao,
  private val eventDao: EventDao,
  private val teamDao: TeamAtEventDao,
  private val prefs: FrcKrawlerPreferences,
  bluetoothAvailabilityProvider: BluetoothAvailabilityProvider
) : ViewModel() {
  var serverConfigState = GameAndEventState()
  var localScoutConfigState = GameAndEventState()

  val bluetoothAvailability = bluetoothAvailabilityProvider.availability

  fun ensureDatabaseInitialized() {
    viewModelScope.launch {
      dbInitializer.ensureInitialized()
    }
  }

  fun loadGamesAndEvents() {
    viewModelScope.launch {
      val previouslySelectedGameId = prefs.lastGameId.firstOrNull()
      gameDao.getAll().collect {
        serverConfigState.availableGames = it
        localScoutConfigState.availableGames = it

        serverConfigState.updateSelectedGame(previouslySelectedGameId)
        localScoutConfigState.updateSelectedGame(previouslySelectedGameId)
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
    val previouslySelectedEventId = prefs.lastEventId.firstOrNull()
    snapshotFlow { selectedGame }
      .onEach { game ->
        game?.let { prefs.setLastGameId(it.id) }
      }
      .flatMapLatest { game ->
        loadingEvents = true
        if (game != null) {
          eventDao.getAllForGame(game.id)
        } else {
          flowOf(emptyList())
        }
      }.collect { events ->
        loadingEvents = false
        availableEvents = events
        updateSelectedEvent(previouslySelectedEventId)
      }
  }

  private suspend fun GameAndEventState.updateTeamsOnEventChange() {
    snapshotFlow { selectedEvent }
      .onEach { event ->
        event?.let { prefs.setLastEventId(it.id) }
      }
      .flatMapLatest { event ->
        loadingTeams = true
        if (event != null) {
          teamDao.getAllTeams(event.id)
        } else {
          flowOf(emptyList())
        }
      }.collect { teams ->
        loadingTeams = false
        hasTeams = teams.isNotEmpty()
      }
  }

  private fun GameAndEventState.updateSelectedGame(defaultGameId: Int?) {
    val preferredGameId = selectedGame?.id ?: defaultGameId
    val matchingGame = availableGames.firstOrNull { it.id == preferredGameId }
    selectedGame = matchingGame
  }

  private fun GameAndEventState.updateSelectedEvent(defaultEventId: Int?) {
    val preferredEventId = selectedEvent?.id ?: defaultEventId
    val matchingEvent = availableEvents.firstOrNull { it.id == preferredEventId }
    selectedEvent = matchingEvent
  }
}
