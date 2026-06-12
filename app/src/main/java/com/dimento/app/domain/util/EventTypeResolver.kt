package com.dimento.app.domain.util

import com.dimento.app.domain.model.EventType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class EventTypeResolver(
    private val zoneId: ZoneId = ZoneId.systemDefault()
) {
    fun resolve(eventDateMillis: Long, nowMillis: Long): EventType {
        val eventDate = Instant.ofEpochMilli(eventDateMillis).atZone(zoneId).toLocalDate()
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        return when {
            eventDate.isBefore(today) -> EventType.PAST
            eventDate.isAfter(today) -> EventType.FUTURE
            eventDateMillis > nowMillis -> EventType.FUTURE
            else -> EventType.TODAY
        }
    }

    fun todayStartEnd(nowMillis: Long): Pair<Long, Long> {
        val today = Instant.ofEpochMilli(nowMillis).atZone(zoneId).toLocalDate()
        val start = today.atStartOfDay(zoneId).toInstant().toEpochMilli()
        val end = today.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli() - 1
        return start to end
    }

    fun formatCsvDate(millis: Long): String {
        return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDateTime().toString()
    }

    fun nowDate(): LocalDate = LocalDate.now(zoneId)
}
