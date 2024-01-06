package com.team2052.frckrawler.ui.event.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.remote.EventService
import com.team2052.frckrawler.data.remote.model.TbaSimpleEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val eventService: EventService,
    private val eventDao: EventDao,
) : ViewModel() {

    private val validYears = 1992..Year.now().value

    private val _state = MutableStateFlow(
        AddEventScreenState(
            years = validYears.toList().reversed()
        )
    )
    val state: StateFlow<AddEventScreenState> = _state

    fun loadEventsForYear(year: Int) {
        _state.value = _state.value.copy(
            events = emptyList(),
            areEventsLoading = true,
            hasNetworkError = false,
        )

        viewModelScope.launch {
            try {
                val result = eventService.getEvents(year)

                when (result.isSuccessful) {
                    true -> {
                        _state.value = _state.value.copy(
                            events = result.body()?.sortedBy { it.name } ?: emptyList(),
                            areEventsLoading = false,
                            hasNetworkError = false,
                        )
                    }
                    false -> {
                        _state.value = _state.value.copy(
                            events = emptyList(),
                            areEventsLoading = false,
                            hasNetworkError = true
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    events = emptyList(),
                    areEventsLoading = false,
                    hasNetworkError = true
                )
            }

        }
    }

    fun saveAutoEvent(gameId: Int, tbaSimpleEvent: TbaSimpleEvent) {
        viewModelScope.launch {
            eventDao.insert(
                Event(
                    name = tbaSimpleEvent.name,
                    tbaId = tbaSimpleEvent.key,
                    gameId = gameId
                )
            )
        }
    }

    fun saveManualEvent(gameId: Int, name: String) {
        viewModelScope.launch {
            eventDao.insert(
                Event(
                    name = name,
                    gameId = gameId
                )
            )
        }
    }
}