package com.dimento.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "reverse_index",
    primaryKeys = ["keyword", "eventId"],
    indices = [Index("eventId"), Index("groupId")]
)
data class ReverseIndexEntity(
    val keyword: String,
    val eventId: Long,
    val groupId: Long
)
