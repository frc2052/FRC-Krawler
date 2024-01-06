package com.team2052.frckrawler.data.remote

import com.team2052.frckrawler.data.remote.model.TbaSimpleEvent
import com.team2052.frckrawler.data.remote.model.TbaSimpleTeam
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EventService {

    @GET("events/{year}/simple")
    suspend fun getEvents(
        @Path("year") year: Int,
    ): Response<List<TbaSimpleEvent>>

    @GET("event/{key}/teams/simple")
    suspend fun getEventTeams(
        @Path("key") eventKey: String,
    ): Response<List<TbaSimpleTeam>>

}