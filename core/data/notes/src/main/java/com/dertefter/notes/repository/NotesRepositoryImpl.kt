package com.dertefter.notes.repository

import android.content.Context
import androidx.core.net.toUri
import com.dertefter.notes.db.NoteDao
import com.dertefter.notes.db.NoteEntity
import com.dertefter.notes.dto.NoteDto
import com.dertefter.notes.dto.SortOrder
import com.dertefter.notes.errors.NoteError
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val dao: NoteDao,
    @ApplicationContext private val context: Context
) : NotesRepository {

    override fun getNotes(query: String, sortOrder: SortOrder): Flow<List<NoteDto>> {
        val isAsc = sortOrder == SortOrder.OLDEST_FIRST
        return dao.getNotes(query, isAsc).map { entities ->
            entities.map { it.toDto() }
        }
    }

    override suspend fun getNoteById(noteId: Long): Flow<NoteDto?> = flow {
        emit(dao.getNoteById(noteId)?.toDto())
    }

    override suspend fun saveNote(
        title: String,
        text: String,
        imagePath: String?,
        noteId: Long?
    ): Result<Unit> = runCatching {
        if (title.isBlank()) throw NoteError.BlankTitle()
        val timestamp = System.currentTimeMillis()
        var finalImagePath: String? = null

        imagePath?.let { path ->
            if (path.startsWith("content://")) {
                val uri = path.toUri()
                val imagesDir = File(context.filesDir, "images")
                if (!imagesDir.exists()) {
                    imagesDir.mkdirs()
                }
                val destFile = File(imagesDir, "${UUID.randomUUID()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                finalImagePath = destFile.absolutePath
            } else {
                val sourceFile = File(path)
                if (sourceFile.exists()) {
                    val imagesDir = File(context.filesDir, "images")
                    if (!imagesDir.exists()) {
                        imagesDir.mkdirs()
                    }

                    if (sourceFile.parentFile?.absolutePath == imagesDir.absolutePath) {
                        finalImagePath = path
                    } else {
                        val destFile = File(imagesDir, "${UUID.randomUUID()}_${sourceFile.name}")
                        sourceFile.copyTo(destFile, overwrite = true)
                        finalImagePath = destFile.absolutePath
                    }
                }
            }
        }

        val entity = NoteEntity(
            id = noteId ?: 0L,
            title = title,
            text = text,
            timestamp = timestamp,
            imagePath = finalImagePath
        )
        dao.insertNote(entity)
    }

    override suspend fun deleteNote(noteId: Long): Result<Unit> = runCatching {
        val note = dao.getNoteById(noteId)
        if (note != null) {
            val imagePath = note.imagePath
            dao.deleteNoteById(noteId)

            if (imagePath != null) {
                val count = dao.countNotesWithImagePath(imagePath)
                if (count == 0) {
                    val file = File(imagePath)
                    if (file.exists()) {
                        file.delete()
                    }
                }
            }
        }
    }

    private fun NoteEntity.toDto(): NoteDto {
        val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())
        return NoteDto(id, title, text, date, imagePath)
    }

}
