package com.dimento.app.domain.model

data class MemoryEvent(
    val id: Long,
    val groupId: Long,
    val text: String,
    val eventDateMillis: Long,
    val recordedDateMillis: Long,
    val completedDateMillis: Long?,
    val voicePath: String?,
    val linkPreviewJson: String? = null
)
