package com.team2052.frckrawler.ui.metrics.edit

import kotlin.math.max
import kotlin.math.min

/**
 * Update the start of the range, ensuring that the end of the range is not smaller than the start
 */
fun IntRange.updateFirst(first: Int): IntRange = first ..max(first, last)

/**
 * Update the end of the range, ensuring the start of the range is not larger than the end
 */
fun IntRange.updateLast(last: Int): IntRange = min(first, last)..last