package com.team2052.frckrawler.data.remote

import com.team2052.frckrawler.data.remote.model.Event
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EventService {

//    @GET("team/{event_key}")
//    suspend fun getEvent(
//        @Path("event_key") eventKey: String,
//    ): Response<Event>
//
//    @GET("team/frc{team_key}/events")
//    suspend fun getEvents(
//        @Path("team_key") team: Int,
//    ): Response<List<Event>>

    @GET("team/frc{team_key}/events/{year}")
    suspend fun getEvents(
        @Path("team_key") team: Int,
        @Path("year") year: Int,
    ): Response<List<Event>>

}