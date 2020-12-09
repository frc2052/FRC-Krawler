package com.team2052.frckrawler.networking

import okhttp3.OkHttpClient
import retrofit2.Retrofit

object ApiManager {
  private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(TbaApiKeyInterceptor())
    .build()

  private val retrofit = Retrofit.Builder()
    .baseUrl("https://www.thebluealliance.com/api/v3")
    .build()

  private val tbaApi = retrofit.create(TbaApi::class.java)
}