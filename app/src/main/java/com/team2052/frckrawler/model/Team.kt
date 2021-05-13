package com.team2052.frckrawler.model

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.Serializable

private const val PRIMARY_KEY = "id"

@Entity(
    tableName = "events",
    indices = [Index(PRIMARY_KEY, unique = true)]
)
@Immutable
data class Team (

    @PrimaryKey
    @ColumnInfo(name = PRIMARY_KEY)
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

    ) : Serializable
