package com.dimento.app.core

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateFormats {
    private val zone: ZoneId = ZoneId.systemDefault()
    private val shortDateFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM")
    private val eventDateTimeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM, hh:mm a")
    private val fullDateTimeFmt: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

    fun shortDateMillis(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(shortDateFmt)

    fun eventDateTimeMillis(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(eventDateTimeFmt)

    fun fullDateTimeMillis(millis: Long): String =
        Instant.ofEpochMilli(millis).atZone(zone).format(fullDateTimeFmt)
}
