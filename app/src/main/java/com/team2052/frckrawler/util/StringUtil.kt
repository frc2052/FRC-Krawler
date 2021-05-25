package com.team2052.frckrawler.util

import java.util.*

fun String.toCamelCase(locale: Locale = Locale.getDefault()): String = split(" ").joinToString(" ") {
    it.capitalize(locale)
}