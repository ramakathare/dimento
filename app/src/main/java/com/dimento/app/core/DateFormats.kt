package com.dimento.app.core

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormats {
    private fun sdf(pattern: String) = SimpleDateFormat(pattern, Locale.getDefault())

    fun shortDateMillis(millis: Long): String = sdf("dd MMM").format(Date(millis))

    fun eventDateTimeMillis(millis: Long): String = sdf("dd MMM, hh:mm a").format(Date(millis))

    fun fullDateTimeMillis(millis: Long): String = sdf("dd MMM yyyy, hh:mm a").format(Date(millis))
}
