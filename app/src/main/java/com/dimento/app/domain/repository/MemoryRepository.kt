package com.dimento.app.domain.repository

import com.dimento.app.domain.model.GroupSummary
import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.domain.model.MemoryGroup
import com.dimento.app.domain.model.SearchResult
import kotlinx.coroutines.flow.Flow

interface MemoryRepository {
    fun observeGroupSummaries(): Flow<List<GroupSummary>>
    fun observeGroups(): Flow<List<MemoryGroup>>
    fun observeEventsByGroup(groupId: Long): Flow<List<MemoryEvent>>
    suspend fun getGroup(groupId: Long): MemoryGroup?
    suspend fun createGroup(name: String): Long
    suspend fun renameGroup(groupId: Long, name: String)
    suspend fun deleteGroup(groupId: Long)

    suspend fun createEvent(
        groupId: Long,
        text: String,
        eventDateMillis: Long,
        recordedDateMillis: Long,
        voicePath: String?
    ): Long

    suspend fun updateEvent(
        eventId: Long,
        text: String,
        eventDateMillis: Long,
        voicePath: String?
    )

    suspend fun markEventCompleted(eventId: Long, completedDateMillis: Long)
    suspend fun deleteEvent(eventId: Long)
    suspend fun forwardEvent(eventId: Long, destinationGroupId: Long, recordedDateMillis: Long): Long
    suspend fun search(query: String, groupId: Long?): SearchResult
    suspend fun getAllEventsWithGroupNames(): List<Pair<MemoryEvent, String>>
    suspend fun getEventsDueToday(startOfDayMillis: Long, endOfDayMillis: Long): List<MemoryEvent>
    suspend fun ensureDefaultGroup()
}
