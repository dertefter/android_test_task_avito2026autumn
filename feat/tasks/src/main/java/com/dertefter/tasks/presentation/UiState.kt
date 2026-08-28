package com.dertefter.tasks.presentation

import com.dertefter.tasks.dto.SortOrder
import com.dertefter.tasks.dto.TaskDto

data class UiState(
    val tasks: List<TaskDto> = emptyList(),
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST,
    val isCreatingTask: Boolean = false,
    val newTaskTitle: String = "",
    val generationStatus: GenerationStatus? = null
)
