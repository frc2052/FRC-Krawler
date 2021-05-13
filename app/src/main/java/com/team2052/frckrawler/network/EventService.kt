package com.team2052.frckrawler.network

import com.team2052.frckrawler.model.Event
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EventService {

    @GET("team/frc{team_key}/events/simple")
    suspend fun getEvents(
        @Path("team_key") team: Int,
    ): List<Event>

    @GET("team/frc{team_key}/events/{year}/simple")
    suspend fun getEvent(
        @Path("team_key") team: Int,
        @Path("year") year: Int,
    ): Response<Event>

}