package com.dimento.app.domain.model

data class GroupSummary(
    val groupId: Long,
    val name: String,
    val icon: String? = null,
    val lastMessage: String?,
    val lastEventDateMillis: Long?,
    val hasFutureEvents: Boolean
)
