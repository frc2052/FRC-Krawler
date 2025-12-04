package com.team2052.frckrawler.ui.event.teams.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.launch

@ContributesIntoMap(AppScope::class)
@ViewModelKey(AddEditTeamViewModel::class)
@Inject
class AddEditTeamViewModel(
  private val teamAtEventDao: TeamAtEventDao,
) : ViewModel() {

  fun saveTeam(
    eventId: Int,
    teamNumber: String,
    teamName: String
  ) {
    viewModelScope.launch {
      teamAtEventDao.insert(
        TeamAtEvent(
          eventId = eventId,
          number = teamNumber,
          name = teamName,
        )
      )
    }
  }

  fun deleteTeam(teamAtEvent: TeamAtEvent) {
    viewModelScope.launch {
      teamAtEventDao.delete(teamAtEvent)
    }
  }
}