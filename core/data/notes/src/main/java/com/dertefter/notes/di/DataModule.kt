package com.dertefter.notes.di

import android.content.Context
import androidx.room.Room
import com.dertefter.notes.db.NoteDao
import com.dertefter.notes.db.NoteDatabase
import com.dertefter.notes.repository.NotesRepository
import com.dertefter.notes.repository.NotesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(@ApplicationContext context: Context): NoteDatabase {
        return Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            "notes.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.dao
    }

    @Provides
    @Singleton
    fun provideNotesRepository(
        dao: NoteDao,
        @ApplicationContext context: Context
    ): NotesRepository {
        return NotesRepositoryImpl(dao, context)
    }
}
