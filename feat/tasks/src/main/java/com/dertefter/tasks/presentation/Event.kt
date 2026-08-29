package com.dertefter.tasks.presentation

import com.dertefter.tasks.dto.SortOrder
import com.dertefter.tasks.dto.TaskFilter

sealed interface Event {
    data object CreateTask : Event
    data class UpdateNewTaskTitle(val title: String) : Event
    data object SaveNewTask : Event
    data object CancelCreateTask : Event
    data class DeleteTask(val taskId: Long) : Event
    data class UpdateSearchQuery(val query: String) : Event
    data object SubmitSearch : Event
    data class ChangeSortOrder(val sortOrder: SortOrder) : Event
    data class ChangeFilter(val filter: TaskFilter) : Event
    data class ToggleTask(val taskId: Long) : Event

    data class StartAiGeneration(val recognizedText: String) : Event
    data object RetryAiGeneration : Event
    data object CancelAiGeneration : Event
}
