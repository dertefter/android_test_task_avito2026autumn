package com.dertefter.tasks.presentation

import com.dertefter.tasks.dto.SortOrder
import com.dertefter.tasks.dto.TaskDto
import com.dertefter.tasks.dto.TaskFilter

data class UiState(
    val tasks: List<TaskDto> = emptyList(),
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,
    val filter: TaskFilter = TaskFilter.ALL,
    val isCreatingTask: Boolean = false,
    val newTaskTitle: String = "",
    val generationStatus: GenerationStatus? = null
)
