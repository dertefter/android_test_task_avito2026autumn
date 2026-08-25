package com.dertefter.notes.presentation

import com.dertefter.notes.dto.SortOrder

sealed interface Event {
    data object CreateNote : Event
    data class DeleteNote(val noteId: Long) : Event
    data class ToggleDeleteMode(val toggleTo: Boolean? = null) : Event
    data class OpenNoteDetail(val noteId: Long) : Event
    data class UpdateSearchQuery(val query: String) : Event
    data object SubmitSearch : Event
    data object ToggleSearch : Event
    data class ChangeSortOrder(val sortOrder: SortOrder) : Event
}
