package com.example.utils

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Locale

fun formatTime(time: LocalTime,prefsManager: PreferencesManager): String{

    val ampmMap = mapOf(
        1L to "PM",
        0L to "AM"
    )

    val twelveHourCustomFormatter: DateTimeFormatter = DateTimeFormatterBuilder()
        .appendPattern("hh:mm ")
        .appendText(
            ChronoField.AMPM_OF_DAY,
            ampmMap
        )
        .toFormatter(Locale.US)
    val textToShow = if(!prefsManager.get24HourFormat()) time.toString() else time.format(twelveHourCustomFormatter)
    return textToShow
}