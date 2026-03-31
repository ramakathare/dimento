package com.dimento.app.domain.model

data class MemoryGroup(
    val id: Long,
    val name: String,
    val icon: String? = null,
    val description: String? = null,
    val createdAtMillis: Long
)
