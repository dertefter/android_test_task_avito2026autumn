package com.dertefter.notes.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("""
        SELECT * FROM notes 
        WHERE :query = '' OR title LIKE '%' || :query || '%' 
        ORDER BY 
            CASE WHEN :isAsc = 1 THEN timestamp END ASC, 
            CASE WHEN :isAsc = 0 THEN timestamp END DESC
    """)
    fun getNotes(query: String, isAsc: Boolean): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: Long): NoteEntity?

    @Query("SELECT COUNT(*) FROM notes WHERE imagePath = :imagePath")
    suspend fun countNotesWithImagePath(imagePath: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: Long)
}
