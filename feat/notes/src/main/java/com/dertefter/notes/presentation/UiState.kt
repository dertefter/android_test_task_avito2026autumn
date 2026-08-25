package com.dertefter.notes.presentation

import com.dertefter.notes.dto.NoteDto
import com.dertefter.notes.dto.SortOrder

data class UiState(
    val notes: List<NoteDto> = emptyList(),
    val isDeleteMode: Boolean = false,
    val searchQuery: String = "",
    val isSearchVisible: Boolean = false,
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST
)
