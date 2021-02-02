package com.team2052.frckrawler.network

import com.team2052.frckrawler.network.model.Match
import com.team2052.frckrawler.network.model.Team
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TbaApi {

    @GET("team/frc{team}")
    suspend fun getTeam(@Path("team") team: Int): Team
    @GET("team/frc{team}/event/{event}/matches")
    suspend fun getMatches(
        @Path("team") team: Int,
        @Path("event") event: String
    ): List<Match>
}