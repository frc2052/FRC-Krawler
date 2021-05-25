package com.team2052.frckrawler.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Event(
    val key: String? = null,
    val name: String? = null,
    @Json(name = "event_code") val eventCode: String? = null,
    @Json(name = "event_type") val eventType: Int? = null,
    val district: District? = null,
    val city: String? = null,
    @Json(name = "state_prov") val stateProv: String? = null,
    val country: String? = null,
    @Json(name = "start_date") val startDate: String? = null,
    @Json(name = "end_date") val endDate: String? = null,
    val year: Int? = null,
    @Json(name = "short_name") val shortName: String? = null,
    @Json(name = "event_type_string") val eventTypeString: String? = null,
    val week: Int? = null,
    val address: String? = null,
    @Json(name = "postal_code") val postalCode: String? = null,
    @Json(name = "gmaps_place_id") val gmapsPlaceId: String? = null,
    @Json(name = "gmaps_url") val gmapsUrl: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    @Json(name = "location_name") val locationName: String? = null,
    val timezone: String? = null,
    val website: String? = null,
    @Json(name = "first_event_id") val firstEventId: String? = null,
    @Json(name = "first_event_code") val firstEventCode: String? = null,
    val webcasts: List<Webcast>? = null,
    @Json(name = "division_keys") val divisionKeys: List<String>? = null,
    @Json(name = "parent_event_key") val parentEventKey: String? = null,
    @Json(name = "playoff_type") val playoffType: Int? = null,
    @Json(name = "playoff_type_string") val playoffTypeString: String? = null,
) {
    companion object {
        fun fake() = Event(
            key = "frc201234",
            name = "201234"
        )
    }
}