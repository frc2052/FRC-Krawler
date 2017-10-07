package com.team2052.frckrawler.metric

import com.team2052.frckrawler.models.Metric
import com.team2052.frckrawler.toJsonObject

data class SliderData(val min: Int, val max: Int)
data class ChooserData(val min: Int, val max: Int, val inc: Int)

fun Metric.getChooserData(): ChooserData? {
    val json = data.toJsonObject()
    if (json != null) return ChooserData(min = json.get("min").asInt, max = json.get("max").asInt, inc = json.get("inc").asInt)
    return null
}

fun Metric.getSliderData(): SliderData? {
    val json = data.toJsonObject()
    if (json != null) return SliderData(min = json.get("min").asInt, max = json.get("max").asInt)
    return null
}
