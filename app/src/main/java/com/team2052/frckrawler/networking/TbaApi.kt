package com.team2052.frckrawler.networking

import com.team2052.frckrawler.networking.model.Team
import retrofit2.http.GET

interface TbaApi {
  @GET("/team/{key}")
  suspend fun getTeam(key: String): Team
}