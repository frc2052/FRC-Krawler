package com.team2052.frckrawler.data.remote

import com.team2052.frckrawler.data.model.Event
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EventService {
    @GET("team/frc{team_key}/events/{year}")
    suspend fun getEvents(
        @Path("team_key") team: Int,
        @Path("year") year: Int,
    ): Response<List<Event>>
}