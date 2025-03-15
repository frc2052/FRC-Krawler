package com.team2052.frckrawler.ui.export

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.export.ExportType
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.prefs.FrcKrawlerPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
  private val gameDao: GameDao,
  private val eventDao: EventDao,
  private val prefs: FrcKrawlerPreferences,
) : ViewModel() {
  var game: Game? by mutableStateOf(null)
  var event: Event? by mutableStateOf(null)
  val includeTeamNames: Flow<Boolean> = prefs.exportIncludeTeamNames
  val includeMatchMetrics: Flow<Boolean> = prefs.exportIncludeMatchMetrics
  val includePitMetrics: Flow<Boolean> = prefs.exportIncludePitMetrics

  fun loadGameAndEvent(gameId: Int, eventId: Int) {
    viewModelScope.launch {
      game = gameDao.get(gameId)
    }

    viewModelScope.launch {
      event = eventDao.get(eventId)
    }
  }

  fun setIncludeTeamNames(includeTeamNames: Boolean) {
    viewModelScope.launch {
      prefs.setExportIncludeTeamNames(includeTeamNames)
    }
  }

  fun setIncludeMatchMetrics(includeMatchMetrics: Boolean) {
    viewModelScope.launch {
      prefs.setExportIncludeMatchMetrics(includeMatchMetrics)
    }
  }

  fun setIncludePitMetrics(includePitMetrics: Boolean) {
    viewModelScope.launch {
      prefs.setExportIncludePitMetrics(includePitMetrics)
    }
  }

  fun exportToFile(type: ExportType, uri: Uri) {
    println("type: $type, uri: $uri")
  }
}