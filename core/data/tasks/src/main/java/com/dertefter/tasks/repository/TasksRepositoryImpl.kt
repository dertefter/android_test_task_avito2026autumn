package com.dertefter.tasks.repository

import com.dertefter.tasks.db.TaskDao
import com.dertefter.tasks.db.TaskEntity
import com.dertefter.tasks.dto.SortOrder
import com.dertefter.tasks.dto.TaskDto
import com.dertefter.tasks.errors.TaskError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val dao: TaskDao
) : TasksRepository {

    override fun getTasks(query: String, sortOrder: SortOrder): Flow<List<TaskDto>> {
        val isAsc = sortOrder == SortOrder.OLDEST_FIRST
        return dao.getTasks(query, isAsc).map { entities ->
            entities.map { it.toDto() }
        }
    }

    override suspend fun getTaskById(taskId: Long): Flow<TaskDto?> = flow {
        emit(dao.getTaskById(taskId)?.toDto())
    }

    override suspend fun saveTask(
        title: String,
        isCompleted: Boolean,
        taskId: Long?
    ): Result<Unit> = runCatching {
        if (title.isBlank()) throw TaskError.BlankTitle()
        val createdAt = if (taskId != null) {
            dao.getTaskById(taskId)?.createdAt ?: System.currentTimeMillis()
        } else {
            System.currentTimeMillis()
        }
        val entity = TaskEntity(
            id = taskId ?: 0L,
            title = title,
            isCompleted = isCompleted,
            createdAt = createdAt
        )
        dao.insertTask(entity)
    }

    override suspend fun deleteTask(taskId: Long): Result<Unit> = runCatching {
        dao.deleteTaskById(taskId)
    }

    override suspend fun toggleTaskCompletion(taskId: Long): Result<Unit> = runCatching {
        val task = dao.getTaskById(taskId)
        if (task != null) {
            dao.updateTaskStatus(taskId, !task.isCompleted)
        }
    }

    private fun TaskEntity.toDto(): TaskDto {
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(createdAt), ZoneId.systemDefault())
        return TaskDto(id, title, isCompleted, date)
    }
}
