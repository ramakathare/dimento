package com.dimento.domain.repository

import com.dimento.domain.model.Event
import com.dimento.domain.model.EventType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface EventRepository {
    fun observeEvents(): Flow<List<Event>>
    fun observeEvent(id: Long): Flow<Event?>
    suspend fun saveEvent(event: Event): Long
    suspend fun deleteEvent(id: Long)
    suspend fun markFutureEventCompleted(id: Long, completionDate: LocalDate)
    suspend fun searchEventIds(query: String): Set<Long>
    suspend fun refreshSearchIndex()
    suspend fun getEventsByIds(ids: Set<Long>): List<Event>
    suspend fun getAllEventsOnce(): List<Event>
    suspend fun getEventsByType(type: EventType): List<Event>
}
