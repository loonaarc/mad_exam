/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package at.ac.hcw.procrastinot.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import at.ac.hcw.procrastinot.data.DefaultTaskRepository
import at.ac.hcw.procrastinot.data.TaskRepository
import at.ac.hcw.procrastinot.data.source.local.TaskDao
import at.ac.hcw.procrastinot.data.source.local.ToDoDatabase
import at.ac.hcw.procrastinot.data.source.network.NetworkDataSource
import at.ac.hcw.procrastinot.data.source.network.TaskNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindTaskRepository(repository: DefaultTaskRepository): TaskRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: TaskNetworkDataSource): NetworkDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): ToDoDatabase {
        val appContext = context.applicationContext
        val seedPreferences = appContext.getSharedPreferences(
            SEED_PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )

        return Room.databaseBuilder(
            appContext,
            ToDoDatabase::class.java,
            "Tasks.db"
        )
            .addMigrations(ToDoDatabase.MIGRATION_1_2)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    if (seedPreferences.getBoolean(KEY_INITIAL_TASKS_SEEDED, false)) {
                        return
                    }

                    if (taskTableIsEmpty(db)) {
                        seedInitialTasks(db)
                    }
                    seedPreferences.edit().putBoolean(KEY_INITIAL_TASKS_SEEDED, true).apply()
                }
            })
            .build()
    }

    @Provides
    fun provideTaskDao(database: ToDoDatabase): TaskDao = database.taskDao()

    private fun taskTableIsEmpty(db: SupportSQLiteDatabase): Boolean {
        val cursor = db.query("SELECT COUNT(*) FROM task")
        return cursor.use {
            it.moveToFirst() && it.getInt(0) == 0
        }
    }

    private fun seedInitialTasks(db: SupportSQLiteDatabase) {
        db.beginTransaction()
        try {
            INITIAL_TASKS.forEach { task ->
                db.execSQL(
                    """
                    INSERT INTO task (id, title, description, isCompleted, priority)
                    VALUES (?, ?, ?, ?, ?)
                    """.trimIndent(),
                    arrayOf<Any>(task.id, task.title, task.description, 0, task.priority)
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }
}

private const val SEED_PREFERENCES_NAME = "task_seed_preferences"
private const val KEY_INITIAL_TASKS_SEEDED = "initial_tasks_seeded"

private data class InitialTask(
    val id: String,
    val title: String,
    val description: String,
    val priority: Int
)

private val INITIAL_TASKS = listOf(
    InitialTask(
        id = "SEED_MAD_EXAM",
        title = "Prepare for MAD exam",
        description = "Review Compose, ViewModel, Room, Hilt, and repository patterns.",
        priority = 1
    ),
    InitialTask(
        id = "SEED_BACKLOG",
        title = "Work through the task backlog",
        description = "Implement one backlog item at a time and commit after each item.",
        priority = 2
    ),
    InitialTask(
        id = "SEED_VERIFY",
        title = "Run build and tests",
        description = "Verify the app with assembleDebug and available test tasks.",
        priority = 3
    )
)

