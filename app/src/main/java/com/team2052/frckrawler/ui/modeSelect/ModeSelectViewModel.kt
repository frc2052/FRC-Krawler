package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.FRCKrawlerApp
import com.team2052.frckrawler.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ModeSelectViewModel @Inject constructor(
    private val context: FRCKrawlerApp,
    private val eventRepository: EventRepository,
) : ViewModel() {

    var isRefreshing by mutableStateOf(false)
    fun refresh() {
        isRefreshing = true
        viewModelScope.launch {
            delay(1000)
            isRefreshing = false
        }
    }

    var expandedCard by mutableStateOf(-1)

    var remoteScoutData: RemoteScoutData by mutableStateOf(RemoteScoutData())
    var serverData: ServerData by mutableStateOf(ServerData())
    var soloScoutData: SoloScoutData by mutableStateOf(SoloScoutData())
}

data class RemoteScoutData(
    val server: String = "",
)

data class SoloScoutData(
    val metricSet: String = "",
    val event: String = "",
)

data class ServerData(
    val teamNumber: String = "",
    val serverName: String = "",
    val metricSet: String = "",
    val event: String = "",
)