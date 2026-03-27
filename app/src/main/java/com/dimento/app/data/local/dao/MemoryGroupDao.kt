package com.dimento.app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dimento.app.data.local.entity.MemoryGroupEntity
import com.dimento.app.data.local.model.GroupSummaryRow
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryGroupDao {
    @Insert
    suspend fun insert(group: MemoryGroupEntity): Long

    @Update
    suspend fun update(group: MemoryGroupEntity)

    @Delete
    suspend fun delete(group: MemoryGroupEntity)

    @Query("SELECT * FROM memory_groups WHERE id = :groupId LIMIT 1")
    suspend fun getById(groupId: Long): MemoryGroupEntity?

    @Query("SELECT * FROM memory_groups ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<MemoryGroupEntity>>

    @Query("SELECT * FROM memory_groups ORDER BY name COLLATE NOCASE ASC")
    suspend fun getAll(): List<MemoryGroupEntity>

    @Query("SELECT * FROM memory_groups WHERE name = :name LIMIT 1")
    suspend fun findByName(name: String): MemoryGroupEntity?

    @Query(
        """
        SELECT
            g.id AS groupId,
            g.name AS name,
            (
                SELECT e.text FROM memory_events e
                WHERE e.groupId = g.id
                ORDER BY e.eventDateMillis DESC, e.id DESC
                LIMIT 1
            ) AS lastMessage,
            (
                SELECT e.eventDateMillis FROM memory_events e
                WHERE e.groupId = g.id
                ORDER BY e.eventDateMillis DESC, e.id DESC
                LIMIT 1
            ) AS lastEventDateMillis,
            (
                SELECT COUNT(*) FROM memory_events e
                WHERE e.groupId = g.id AND e.eventDateMillis > :nowMillis
            ) AS futureEventCount
        FROM memory_groups g
        ORDER BY COALESCE(lastEventDateMillis, 0) DESC, g.name COLLATE NOCASE ASC
        """
    )
    fun observeGroupSummaries(nowMillis: Long): Flow<List<GroupSummaryRow>>
}
