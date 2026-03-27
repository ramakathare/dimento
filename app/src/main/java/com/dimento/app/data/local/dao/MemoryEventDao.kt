package com.dimento.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dimento.app.data.local.entity.MemoryEventEntity
import com.dimento.app.data.local.model.EventWithGroupNameRow
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryEventDao {
    @Insert
    suspend fun insert(event: MemoryEventEntity): Long

    @Update
    suspend fun update(event: MemoryEventEntity)

    @Query("DELETE FROM memory_events WHERE id = :eventId")
    suspend fun deleteById(eventId: Long)

    @Query("SELECT * FROM memory_events WHERE id = :eventId LIMIT 1")
    suspend fun getById(eventId: Long): MemoryEventEntity?

    @Query(
        """
        SELECT * FROM memory_events
        WHERE groupId = :groupId
        ORDER BY eventDateMillis ASC, id ASC
        """
    )
    fun observeByGroup(groupId: Long): Flow<List<MemoryEventEntity>>

    @Query(
        """
        SELECT e.*, g.name AS groupName
        FROM memory_events e
        INNER JOIN memory_groups g ON g.id = e.groupId
        ORDER BY e.eventDateMillis ASC, e.id ASC
        """
    )
    suspend fun getAllWithGroupNames(): List<EventWithGroupNameRow>

    @Query(
        """
        SELECT * FROM memory_events
        WHERE eventDateMillis BETWEEN :startOfDayMillis AND :endOfDayMillis
          AND completedDateMillis IS NULL
        ORDER BY eventDateMillis ASC
        """
    )
    suspend fun getDueToday(startOfDayMillis: Long, endOfDayMillis: Long): List<MemoryEventEntity>
}
