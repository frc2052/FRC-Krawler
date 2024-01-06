package com.team2052.frckrawler.ui.event.teams.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditTeamViewModel @Inject constructor(
    private val teamAtEventDao: TeamAtEventDao,
) : ViewModel() {

    fun saveTeam(
        eventId: Int,
        teamNumber: Int,
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