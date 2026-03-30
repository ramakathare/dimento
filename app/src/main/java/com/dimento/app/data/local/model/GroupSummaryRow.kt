package com.dimento.app.data.local.model

data class GroupSummaryRow(
    val groupId: Long,
    val name: String,
    val icon: String?,
    val lastMessage: String?,
    val lastEventDateMillis: Long?,
    val futureEventCount: Int
)
