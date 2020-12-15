package com.team2052.frckrawler.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class TbaApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
            .newBuilder()
            .addHeader("X-TBA-Auth-Key", "MTnxl1VUPAEseVOl8UGFhM7MjFJfJ1hNMxGd4BF8Lyt3fzqpKkFynemdPMHDyyXA")
            .build()

        return chain.proceed(request)
    }

}