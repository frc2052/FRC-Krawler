package com.team2052.frckrawler.repository

import com.team2052.frckrawler.data.remote.model.Event
import com.team2052.frckrawler.data.remote.EventService
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class EventRepository @Inject constructor(
    private val eventService: EventService,
) {

    // TODO: implement CacheControl (maybe) and use Resource or NetworkBoundResource to contain response
//    suspend fun getEventList(team: Int): List<Event>? =
//        eventService.getEvents(team).body()

//    suspend fun test() {
//        Log.d("TEST_TAG", eventService.getEvent(2052, 2021).toString())
//    }

//    suspend fun getEvent(team: Int, year: Int): Event =
//        eventService.getEvent(team, year)

    suspend fun getEvents(): List<Event> {
        val response = eventService.getEvents(2052, 2021)
        return response.body() ?: emptyList()
    }
}