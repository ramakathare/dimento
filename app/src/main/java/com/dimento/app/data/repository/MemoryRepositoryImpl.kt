package com.dimento.app.data.repository

import androidx.room.withTransaction
import com.dimento.app.data.local.DiMentoDatabase
import com.dimento.app.data.local.entity.MemoryEventEntity
import com.dimento.app.data.local.entity.MemoryGroupEntity
import com.dimento.app.data.local.entity.ReverseIndexEntity
import com.dimento.app.data.mapper.toDomain
import com.dimento.app.data.search.KeywordTokenizer
import com.dimento.app.domain.model.GroupSummary
import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.domain.model.MemoryGroup
import com.dimento.app.domain.model.SearchResult
import com.dimento.app.domain.repository.MemoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MemoryRepositoryImpl(
    private val database: DiMentoDatabase,
    private val tokenizer: KeywordTokenizer
) : MemoryRepository {
    private val groupDao = database.memoryGroupDao()
    private val eventDao = database.memoryEventDao()
    private val reverseIndexDao = database.reverseIndexDao()

    override fun observeGroupSummaries(): Flow<List<GroupSummary>> {
        return groupDao.observeGroupSummaries(System.currentTimeMillis())
            .map { rows -> rows.map { it.toDomain() } }
    }

    override fun observeGroups(): Flow<List<MemoryGroup>> {
        return groupDao.observeAll().map { groups -> groups.map { it.toDomain() } }
    }

    override fun observeEventsByGroup(groupId: Long): Flow<List<MemoryEvent>> {
        return eventDao.observeByGroup(groupId).map { events -> events.map { it.toDomain() } }
    }

    override suspend fun getGroup(groupId: Long): MemoryGroup? = groupDao.getById(groupId)?.toDomain()

    override suspend fun createGroup(name: String, icon: String?, description: String?): Long {
        return groupDao.insert(
            MemoryGroupEntity(
                name = name,
                icon = icon,
                description = description,
                createdAtMillis = System.currentTimeMillis()
            )
        )
    }

    override suspend fun renameGroup(groupId: Long, name: String, icon: String?, description: String?) {
        val group = groupDao.getById(groupId) ?: return
        groupDao.update(group.copy(name = name, icon = icon, description = description))
    }

    override suspend fun deleteGroup(groupId: Long) {
        val group = groupDao.getById(groupId) ?: return
        groupDao.delete(group)
    }

    override suspend fun createEvent(
        groupId: Long,
        text: String,
        eventDateMillis: Long,
        recordedDateMillis: Long,
        voicePath: String?
    ): Long {
        return database.withTransaction {
            val eventId = eventDao.insert(
                MemoryEventEntity(
                    groupId = groupId,
                    text = text,
                    eventDateMillis = eventDateMillis,
                    recordedDateMillis = recordedDateMillis,
                    completedDateMillis = null,
                    voicePath = voicePath
                )
            )
            rebuildReverseIndexForEvent(eventId)
            eventId
        }
    }

    override suspend fun updateEvent(eventId: Long, text: String, eventDateMillis: Long, voicePath: String?) {
        database.withTransaction {
            val event = eventDao.getById(eventId) ?: return@withTransaction
            eventDao.update(
                event.copy(
                    text = text,
                    eventDateMillis = eventDateMillis,
                    voicePath = voicePath
                )
            )
            rebuildReverseIndexForEvent(eventId)
        }
    }

    override suspend fun markEventCompleted(eventId: Long, completedDateMillis: Long) {
        val event = eventDao.getById(eventId) ?: return
        eventDao.update(event.copy(completedDateMillis = completedDateMillis))
    }

    override suspend fun deleteEvent(eventId: Long) {
        database.withTransaction {
            reverseIndexDao.deleteForEvent(eventId)
            eventDao.deleteById(eventId)
        }
    }

    override suspend fun forwardEvent(eventId: Long, destinationGroupId: Long, recordedDateMillis: Long): Long {
        return database.withTransaction {
            val source = eventDao.getById(eventId) ?: return@withTransaction -1L
            val newId = eventDao.insert(
                source.copy(
                    id = 0,
                    groupId = destinationGroupId,
                    recordedDateMillis = recordedDateMillis,
                    completedDateMillis = null
                )
            )
            rebuildReverseIndexForEvent(newId)
            newId
        }
    }

    override suspend fun search(query: String, groupId: Long?): SearchResult {
        val normalized = query.lowercase().trim()
        val groupsSnapshot = groupDao.getAll()
            .filter { groupId == null || it.id == groupId }
            .filter { it.name.lowercase().contains(normalized) }
            .map { it.toDomain() }
        val tokens = tokenizer.tokenize(normalized).ifEmpty { setOf(normalized) }
        val seen = linkedSetOf<Long>()
        val events = tokens.flatMap { token ->
            reverseIndexDao.searchEvents(token, groupId)
        }.filter { seen.add(it.id) }.map {
            SearchResult.MatchedEvent(
                event = it.toEventEntity().toDomain(),
                groupName = it.groupName
            )
        }
        return SearchResult(groups = groupsSnapshot, matchedEvents = events)
    }

    override suspend fun getAllEventsWithGroupNames(): List<Pair<MemoryEvent, String>> {
        return eventDao.getAllWithGroupNames().map { row ->
            row.toEventEntity().toDomain() to row.groupName
        }
    }

    override suspend fun getEventsWithGroupName(groupId: Long): List<Pair<MemoryEvent, String>> {
        return eventDao.getAllWithGroupNames()
            .filter { it.groupId == groupId }
            .map { row -> row.toEventEntity().toDomain() to row.groupName }
    }

    override suspend fun getEventsDueToday(startOfDayMillis: Long, endOfDayMillis: Long): List<MemoryEvent> {
        return eventDao.getDueToday(startOfDayMillis, endOfDayMillis).map { it.toDomain() }
    }

    override suspend fun ensureDefaultGroup() {
        if (groupDao.findByName(DEFAULT_GROUP_NAME) == null) {
            groupDao.insert(
                MemoryGroupEntity(
                    name = DEFAULT_GROUP_NAME,
                    createdAtMillis = System.currentTimeMillis()
                )
            )
        }
    }

    private suspend fun rebuildReverseIndexForEvent(eventId: Long) {
        reverseIndexDao.deleteForEvent(eventId)
        val event = eventDao.getById(eventId) ?: return
        val tokens = tokenizer.tokenize(event.text)
        if (tokens.isEmpty()) return
        val indexRows = tokens.map { token ->
            ReverseIndexEntity(keyword = token, eventId = event.id, groupId = event.groupId)
        }
        reverseIndexDao.insertAll(indexRows)
    }

    private companion object {
        const val DEFAULT_GROUP_NAME = "General"
    }
}
