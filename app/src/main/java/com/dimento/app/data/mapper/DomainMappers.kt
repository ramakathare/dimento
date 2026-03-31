package com.dimento.app.data.mapper

import com.dimento.app.data.local.entity.MemoryEventEntity
import com.dimento.app.data.local.entity.MemoryGroupEntity
import com.dimento.app.data.local.model.GroupSummaryRow
import com.dimento.app.domain.model.GroupSummary
import com.dimento.app.domain.model.MemoryEvent
import com.dimento.app.domain.model.MemoryGroup

fun MemoryGroupEntity.toDomain(): MemoryGroup = MemoryGroup(
    id = id,
    name = name,
    icon = icon,
    description = description,
    createdAtMillis = createdAtMillis
)

fun MemoryEventEntity.toDomain(): MemoryEvent = MemoryEvent(
    id = id,
    groupId = groupId,
    text = text,
    eventDateMillis = eventDateMillis,
    recordedDateMillis = recordedDateMillis,
    completedDateMillis = completedDateMillis,
    voicePath = voicePath
)

fun GroupSummaryRow.toDomain(): GroupSummary = GroupSummary(
    groupId = groupId,
    name = name,
    icon = icon,
    description = description,
    lastMessage = lastMessage,
    lastEventDateMillis = lastEventDateMillis,
    hasFutureEvents = futureEventCount > 0
)
