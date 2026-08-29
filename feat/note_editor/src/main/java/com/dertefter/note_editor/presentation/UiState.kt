package com.dertefter.note_editor.presentation

import com.dertefter.data.settings.dto.note_editor.NoteEditorStrategy

data class UiState(
    val id: Long? = null,
    val title: String = "",
    val text: String = "",
    val imagePath: String? = null,
    val isLoading: Boolean = false,
    val isSaveEnabled: Boolean = false,
    val error: Throwable? = null,
    val strategy: NoteEditorStrategy = NoteEditorStrategy.WITHOUT_PERMISSIONS
)
