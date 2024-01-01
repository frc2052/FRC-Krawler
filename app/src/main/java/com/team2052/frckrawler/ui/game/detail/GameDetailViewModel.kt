package com.team2052.frckrawler.ui.game.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.MetricSet
import com.team2052.frckrawler.data.local.MetricSetDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val gameDao: GameDao,
    private val eventDao: EventDao,
    private val metricSetDao: MetricSetDao
) : ViewModel() {

    private val _game = MutableStateFlow<Game?>(null)
    val game: StateFlow<Game?> = _game

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _metricSets = MutableStateFlow<List<MetricSet>>(emptyList())
    val metricSets: StateFlow<List<MetricSet>> = _metricSets

    private var gameId: Int = 0

    fun loadGame(gameId: Int) {
        this.gameId = gameId

        viewModelScope.launch {
            _game.value = gameDao.get(gameId)
        }

        viewModelScope.launch {
            eventDao.getAllForGame(gameId).collect {
                _events.value = it
            }
        }

        viewModelScope.launch {
            metricSetDao.getAllForGame(gameId).collect {
                _metricSets.value = it
            }
        }
    }

    fun createNewMetricSet(name: String) {
        viewModelScope.launch {
            metricSetDao.insert(
                MetricSet(
                    name = name,
                    gameId = gameId
                )
            )
        }
    }

}