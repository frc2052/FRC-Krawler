package com.team2052.frckrawler.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class Event(
    @PrimaryKey @Json(name = "key") val key: String,
    @ColumnInfo @Json(name = "name") val name: String?
) {
    companion object {
        val fakeEvent = Event(
            key = "2022MNDU",
            name = "Lake Superior Regional"
        )
    }
}