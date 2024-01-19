package com.team2052.frckrawler.data.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeJsonAdapter {
    @ToJson
    fun toJson(value: ZonedDateTime): Long {
        return value.toEpochSecond()
    }

    @FromJson
    fun fromJson(value: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault())
    }
}