package com.dimento.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "memory_events",
    foreignKeys = [
        ForeignKey(
            entity = MemoryGroupEntity::class,
            parentColumns = ["id"],
            childColumns = ["groupId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("groupId"), Index("eventDateMillis")]
)
data class MemoryEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val groupId: Long,
    val text: String,
    val eventDateMillis: Long,
    val recordedDateMillis: Long,
    val completedDateMillis: Long?,
    val voicePath: String?
)
