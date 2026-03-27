package com.dimento.app.data.local.model

import com.dimento.app.data.local.entity.MemoryEventEntity

data class EventWithGroupNameRow(
    val id: Long,
    val groupId: Long,
    val text: String,
    val eventDateMillis: Long,
    val recordedDateMillis: Long,
    val completedDateMillis: Long?,
    val voicePath: String?,
    val groupName: String
) {
    fun toEventEntity(): MemoryEventEntity = MemoryEventEntity(
        id = id,
        groupId = groupId,
        text = text,
        eventDateMillis = eventDateMillis,
        recordedDateMillis = recordedDateMillis,
        completedDateMillis = completedDateMillis,
        voicePath = voicePath
    )
}
