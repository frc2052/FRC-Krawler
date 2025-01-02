package com.team2052.frckrawler.domain

import com.team2052.frckrawler.data.local.Event
import com.team2052.frckrawler.data.local.EventDao
import com.team2052.frckrawler.data.local.TeamAtEvent
import com.team2052.frckrawler.data.local.TeamAtEventDao
import com.team2052.frckrawler.data.remote.EventService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * This use case supports creating an event. For events with a TBA event ID, this will also
 * download a team list.
 */
class CreateEventUseCase @Inject constructor(
  private val eventDao: EventDao,
  private val teamAtEventDao: TeamAtEventDao,
  private val eventService: EventService,
) {

  suspend operator fun invoke(
    name: String,
    gameId: Int,
    tbaId: String? = null
  ) {
    val eventId = eventDao.insert(
      Event(
        name = name,
        tbaId = tbaId,
        gameId = gameId
      )
    )

    if (tbaId != null) {
      saveTeamsForEvent(
        eventId = eventId.toInt(),
        tbaId = tbaId
      )
    }
  }

  private suspend fun saveTeamsForEvent(eventId: Int, tbaId: String) {
    try {
      val teams = eventService.getEventTeams(tbaId)
      if (teams.isSuccessful) {
        teams.body()?.forEach { team ->
          teamAtEventDao.insert(
            TeamAtEvent(
              number = team.number,
              name = team.nickname,
              eventId = eventId
            )
          )
        }
      }

    } catch (e: Exception) {
      // TODO log this
    }
  }
}