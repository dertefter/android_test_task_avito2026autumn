package com.dertefter.tasks.repository

import com.dertefter.tasks.dto.SortOrder
import com.dertefter.tasks.dto.TaskDto
import kotlinx.coroutines.flow.Flow

interface TasksRepository {
    fun getTasks(query: String = "", sortOrder: SortOrder = SortOrder.NEWEST_FIRST): Flow<List<TaskDto>>

    suspend fun getTaskById(taskId: Long): Flow<TaskDto?>

    suspend fun saveTask(
        title: String,
        isCompleted: Boolean = false,
        taskId: Long? = null
    ): Result<Unit>

    suspend fun deleteTask(taskId: Long): Result<Unit>

    suspend fun toggleTaskCompletion(taskId: Long): Result<Unit>
}
