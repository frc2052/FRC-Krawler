package com.team2052.frckrawler.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity
@JsonClass(generateAdapter = true)
data class Metric(
    @PrimaryKey(autoGenerate = true) @Json val id: Int,
    @ColumnInfo @Json val name: String,
    @ColumnInfo @Json val type: MetricType,
    @ColumnInfo @Json val defaultValue: String,
) {
    companion object {
        val fakeMetric = Metric(
            id = 0,
            name = "Lake Superior Regional",
            type = MetricType.Boolean,
            defaultValue = "true",
        )
    }
}
