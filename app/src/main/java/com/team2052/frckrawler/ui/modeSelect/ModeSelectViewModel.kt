package com.team2052.frckrawler.ui.modeSelect

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.FRCKrawlerApp
import com.team2052.frckrawler.bluetooth.BluetoothController
import com.team2052.frckrawler.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class ModeSelectViewModel @Inject constructor(
    private val context: FRCKrawlerApp,
    private val eventRepository: EventRepository,
    val bluetoothController: BluetoothController,
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
    val game: String = "",
    val event: String = "",
)

data class ServerData(
    val teamNumber: String = "",
    val serverName: String = "",
    val game: String = "",
    val event: String = "",
)