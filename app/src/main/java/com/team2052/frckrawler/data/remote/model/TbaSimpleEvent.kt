package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TbaSimpleEvent(
    val key: String? = null,
    val name: String? = null,
    @Json(name = "event_code") val eventCode: String? = null,
) {
    companion object {
        fun fake() = TbaSimpleEvent(
            key = "frc201234",
            name = "201234",
        )
    }
}