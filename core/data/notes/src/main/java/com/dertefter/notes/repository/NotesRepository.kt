package com.dertefter.notes.repository

import com.dertefter.notes.dto.NoteDto
import com.dertefter.notes.dto.SortOrder
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getNotes(query: String = "", sortOrder: SortOrder = SortOrder.NEWEST_FIRST): Flow<List<NoteDto>>

    suspend fun getNoteById(noteId: Long):  Flow<NoteDto?>

    suspend fun saveNote(
        title: String,
        text: String,
        imagePath: String?,
        noteId: Long? = null
    ): Result<Unit>

    suspend fun deleteNote(noteId: Long): Result<Unit>
}
