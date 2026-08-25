package com.dertefter.tasks.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TaskEntity::class],
    version = 5,
    exportSchema = false
)
abstract class TaskDatabase : RoomDatabase() {
    abstract val dao: TaskDao
}
