package com.team2052.frckrawler.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Represents an event both as a network response and a room entity
 *
 * @author matt
 */
@Entity(
    tableName = "events",
    indices = [Index("id", unique = true)]
)
@Immutable
data class Event (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "key")
    @SerializedName("key")
    val key: String,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val name: String,

    @ColumnInfo(name = "code")
    @SerializedName("event_code")
    val code: String,

    @ColumnInfo(name = "type")
    @SerializedName("event_type")
    val type: String,

) : Serializable {
    companion object {
        fun fake() = Event(
            key = "",
            name = "",
            code = "",
            type = "",
        )
    }
}