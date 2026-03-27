package com.dimento.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dimento.app.data.local.entity.ReverseIndexEntity
import com.dimento.app.data.local.model.EventWithGroupNameRow

@Dao
interface ReverseIndexDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ReverseIndexEntity>)

    @Query("DELETE FROM reverse_index WHERE eventId = :eventId")
    suspend fun deleteForEvent(eventId: Long)

    @Query("DELETE FROM reverse_index")
    suspend fun clearAll()

    @Query(
        """
        SELECT DISTINCT e.id, e.groupId, e.text, e.eventDateMillis, e.recordedDateMillis, e.completedDateMillis, e.voicePath, g.name AS groupName
        FROM reverse_index idx
        INNER JOIN memory_events e ON e.id = idx.eventId
        INNER JOIN memory_groups g ON g.id = e.groupId
        WHERE idx.keyword LIKE :query || '%'
          AND (:groupId IS NULL OR idx.groupId = :groupId)
        ORDER BY e.eventDateMillis DESC, e.id DESC
        """
    )
    suspend fun searchEvents(query: String, groupId: Long?): List<EventWithGroupNameRow>
}
