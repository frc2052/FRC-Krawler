package com.team2052.frckrawler.data

import com.team2052.frckrawler.data.model.Event
import com.team2052.frckrawler.data.provider.EventDAO
import com.team2052.frckrawler.data.provider.EventService

class EventRepository(
    private val eventService: EventService,
    private val eventDAO: EventDAO
) {
    suspend fun getEvent(team: Int, name: String): Event? {
        var event = eventDAO.getEvent(name)
        if (event == null) {
            event = eventService.getEvent(team, name)
            if (event != null) {
                eventDAO.insert(event)
            }
        }
        return event
    }
}