package com.dimento.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dimento.app.data.local.dao.MemoryEventDao
import com.dimento.app.data.local.dao.MemoryGroupDao
import com.dimento.app.data.local.dao.ReverseIndexDao
import com.dimento.app.data.local.entity.MemoryEventEntity
import com.dimento.app.data.local.entity.MemoryGroupEntity
import com.dimento.app.data.local.entity.ReverseIndexEntity

@Database(
    entities = [MemoryGroupEntity::class, MemoryEventEntity::class, ReverseIndexEntity::class],
    version = 2,
    exportSchema = false
)
abstract class DiMentoDatabase : RoomDatabase() {
    abstract fun memoryGroupDao(): MemoryGroupDao
    abstract fun memoryEventDao(): MemoryEventDao
    abstract fun reverseIndexDao(): ReverseIndexDao
}
