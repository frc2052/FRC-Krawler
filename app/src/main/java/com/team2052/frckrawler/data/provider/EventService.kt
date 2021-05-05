package com.team2052.frckrawler.data.provider

import com.team2052.frckrawler.data.model.Event
import retrofit2.http.GET
import retrofit2.http.Path

interface EventService {
    @GET("team/frc{team}/events/{event}")
    suspend fun getEvent(
        @Path("team") team: Int,
        @Path("event") name: String
    ): Event?
}