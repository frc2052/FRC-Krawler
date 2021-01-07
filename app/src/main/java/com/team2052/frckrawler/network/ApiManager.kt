package com.team2052.frckrawler.network

import com.team2052.frckrawler.network.interceptors.TbaApiKeyInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object ApiManager {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(TbaApiKeyInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://www.thebluealliance.com/api/v3/")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val tbaApi: TbaApi = retrofit.create(TbaApi::class.java)
}