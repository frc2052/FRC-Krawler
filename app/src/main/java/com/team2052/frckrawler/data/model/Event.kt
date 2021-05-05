package com.team2052.frckrawler.data.model

import android.location.Location
import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

private const val PRIMARY_KEY = "id"

@Entity(
    tableName = "events",
    indices = [
        Index(PRIMARY_KEY, unique = true)
    ]
)
@Immutable
data class Event (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = PRIMARY_KEY) val id: Long = 0L,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "game") val game: String,
    //@ColumnInfo(name = "location") val location: Location,
) : Serializable