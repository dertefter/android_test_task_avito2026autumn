package com.dertefter.tasks.di

import android.content.Context
import androidx.room.Room
import com.dertefter.tasks.db.TaskDao
import com.dertefter.tasks.db.TaskDatabase
import com.dertefter.tasks.repository.TasksRepository
import com.dertefter.tasks.repository.TasksRepositoryImpl
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
    fun provideTaskDatabase(@ApplicationContext context: Context): TaskDatabase {
        return Room.databaseBuilder(
            context,
            TaskDatabase::class.java,
            "tasks.db"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    fun provideTaskDao(database: TaskDatabase): TaskDao {
        return database.dao
    }

    @Provides
    @Singleton
    fun provideTasksRepository(
        dao: TaskDao
    ): TasksRepository {
        return TasksRepositoryImpl(dao)
    }
}
