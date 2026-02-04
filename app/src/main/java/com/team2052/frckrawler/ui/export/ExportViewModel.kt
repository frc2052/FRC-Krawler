package com.team2052.frckrawler.ui.export

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.export.DataExporter
import com.team2052.frckrawler.data.export.ExportType
import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.Game
import com.team2052.frckrawler.data.local.GameDao
import com.team2052.frckrawler.data.local.prefs.FrcKrawlerPreferences
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(ExportViewModel::class)
@Inject
class ExportViewModel(
  private val gameDao: GameDao,
  private val eventDao: EventDao,
  private val prefs: FrcKrawlerPreferences,
  private val exporter: DataExporter,
) : ViewModel() {
  var isExporting: Boolean by mutableStateOf(false)
  var game: Game? by mutableStateOf(null)
  var event: Event? by mutableStateOf(null)
  val includeTeamNames: Flow<Boolean> = prefs.exportIncludeTeamNames
  val includeMatchMetrics: Flow<Boolean> = prefs.exportIncludeMatchMetrics
  val includePitMetrics: Flow<Boolean> = prefs.exportIncludePitMetrics
  val includeDeleted: Flow<Boolean> = prefs.exportIncludeDeleted

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

  fun setIncludeDeleted(includeDeleted: Boolean) {
    viewModelScope.launch {
      prefs.setExportIncludeDeleted(includeDeleted)
    }
  }

  fun exportToFile(type: ExportType, uri: Uri) {
    val eventId = event?.id ?: return
    viewModelScope.launch {
      isExporting = true
      exporter.export(
        fileUri = uri,
        type = type,
        eventId = eventId
      )
      isExporting = false
    }
  }
}