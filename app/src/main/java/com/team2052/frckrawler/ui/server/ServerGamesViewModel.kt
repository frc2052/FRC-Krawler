package com.team2052.frckrawler.ui.server

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.MetricSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ServerGamesViewModel @Inject constructor(
    private val metricSetDao: MetricSetDao
): ViewModel() {
    var metricSets: List<MetricSet> by mutableStateOf(emptyList())

    fun loadGames() {
        viewModelScope.launch {
            metricSetDao.getAll().collect() {
                metricSets = it
            }
        }
    }

    fun makeGame(name: String) {
        viewModelScope.launch {
            metricSetDao.insert(MetricSet(name = name))
        }
        loadGames()
    }

}