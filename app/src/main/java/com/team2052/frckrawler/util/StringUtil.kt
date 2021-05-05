package com.team2052.frckrawler.util

import java.util.*

fun String.camelcase(locale: Locale = Locale.getDefault()): String = split(" ").joinToString(" ") {
    it.toLowerCase(locale).capitalize(locale)
}