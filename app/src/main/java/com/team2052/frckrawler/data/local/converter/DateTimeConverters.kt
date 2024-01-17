package com.team2052.frckrawler.data.local.converter

import androidx.room.TypeConverter
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class DateTimeConverters {

    @TypeConverter
    fun toZonedDateTime(value: Long?): ZonedDateTime? {
        return value?.let {
            ZonedDateTime.ofInstant(Instant.ofEpochSecond(value), ZoneId.systemDefault())
        }
    }

    @TypeConverter
    fun fromZonedDateTime(value: ZonedDateTime?): Long? {
        return value?.toEpochSecond()
    }
}