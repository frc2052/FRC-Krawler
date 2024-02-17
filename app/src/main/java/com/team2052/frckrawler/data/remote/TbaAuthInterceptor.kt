package com.team2052.frckrawler.data.remote

import com.team2052.frckrawler.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class TbaAuthInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val request = chain.request()
      .newBuilder()
      .addHeader("X-TBA-Auth-Key", BuildConfig.TBA_API_KEY)
      .build()
    return chain.proceed(request)
  }
}