package com.team2052.frckrawler.data.model

fun Metric.isSectionHeader(): Boolean {
    return this is Metric.SectionHeader || isLegacyHeader()
}

/**
 * FRCKrawler v3 used empty chooser/checkbox metrics to represent section headers
 */
private fun Metric.isLegacyHeader(): Boolean {
    return this is Metric.ChooserMetric && (options.isEmpty() || options == listOf(""))
            || this is Metric.CheckboxMetric && (options.isEmpty() || options == listOf(""))
}