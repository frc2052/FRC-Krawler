package com.team2052.frckrawler.repository

import com.team2052.frckrawler.model.Event
import com.team2052.frckrawler.network.EventService
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class EventRepository @Inject constructor(
    private val eventService: EventService,
) {

    // TODO: implement CacheControl (maybe) and use Resource or NetworkBoundResource to contain response
    suspend fun getEventList(team: Int): List<Event> =
        eventService.getEvents(team)

//    suspend fun getEvent(team: Int, year: Int): Event =
//        tbaService.getEvent(team, year)

}