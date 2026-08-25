package com.dertefter.tasks.presentation

import com.dertefter.tasks.dto.SortOrder

sealed interface Event {
    data object CreateTask : Event
    data class UpdateNewTaskTitle(val title: String) : Event
    data object SaveNewTask : Event
    data object CancelCreateTask : Event
    data class DeleteTask(val taskId: Long) : Event
    data class UpdateSearchQuery(val query: String) : Event
    data object SubmitSearch : Event
    data object ToggleSearch : Event
    data class ChangeSortOrder(val sortOrder: SortOrder) : Event
    data class ToggleTask(val taskId: Long) : Event
}
