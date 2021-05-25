package com.team2052.frckrawler.data.remote

import com.team2052.frckrawler.data.remote.model.Status
import retrofit2.Response
import retrofit2.http.GET

interface StatusService {

    @GET("status")
    suspend fun getEvents(): Response<Status>

}