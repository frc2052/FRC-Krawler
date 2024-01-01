package com.team2052.frckrawler.ui.event.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.remote.EventService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class AddEventViewModel @Inject constructor(
    private val eventService: EventService
) : ViewModel() {

    private val validYears = 1992..Year.now().value

    private val _state = MutableStateFlow(
        AddEventScreenState(
            years = validYears.toList().reversed()
        )
    )
    val state: StateFlow<AddEventScreenState> = _state

    fun loadEventsForYear(year: Int) {
        viewModelScope.launch {
            val result = eventService.getEvents(year)

            when (result.isSuccessful) {
                true -> {
                    _state.value = _state.value.copy(
                        events = result.body() ?: emptyList(),
                        hasNetworkError = false
                    )
                }
                false -> {
                    _state.value = _state.value.copy(
                        events = result.body() ?: emptyList(),
                        hasNetworkError = true
                    )
                }
            }

        }
    }
}