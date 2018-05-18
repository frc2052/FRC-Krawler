package com.team2052.frckrawler.core.metrics

import com.team2052.frckrawler.core.common.v3.JSON
import com.team2052.frckrawler.core.data.models.Metric

data class SliderData(val min: Int, val max: Int)
data class ChooserData(val min: Int, val max: Int, val inc: Int)

fun Metric.getChooserData(): ChooserData? {
    val json = JSON.getAsJsonObject(data)
    if (json != null) return com.team2052.frckrawler.core.metrics.ChooserData(min = json.get("min").asInt, max = json.get("max").asInt, inc = json.get("inc").asInt)
    return null
}

fun Metric.getSliderData(): SliderData? {
    val json = JSON.getAsJsonObject(data)
    if (json != null) return com.team2052.frckrawler.core.metrics.SliderData(min = json.get("min").asInt, max = json.get("max").asInt)
    return null
}
