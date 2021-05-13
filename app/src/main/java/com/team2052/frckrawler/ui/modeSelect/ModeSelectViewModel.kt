package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.FRCKrawlerApp
import com.team2052.frckrawler.model.Event
import com.team2052.frckrawler.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModeSelectViewModel @Inject constructor(
    private val context: FRCKrawlerApp,
    private val eventRepository: EventRepository,
) : ViewModel() {

    val events: MutableState<List<Event>> = mutableStateOf(emptyList())

    fun networkTest() {
        viewModelScope.launch {
            val result = eventRepository.getEventList(2052)
            events.value = result
        }
    }

    // TODO: Implement Data Store
}