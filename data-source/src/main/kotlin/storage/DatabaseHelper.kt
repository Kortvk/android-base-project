package ru.appkode.base.data.storage

import android.content.Context
import androidx.room.Room
import ru.appkode.base.data.storage.db.AppDatabase
import ru.appkode.base.data.storage.db.DATABASE_NAME
import ru.appkode.base.data.storage.persistence.task.MoviePersistence
import ru.appkode.base.data.storage.persistence.task.TaskPersistence

object DatabaseHelper {

  private lateinit var database: AppDatabase

  fun createDatabase(context: Context) {
    database = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
  }

  fun getTaskPersistence(): TaskPersistence = database.taskPersistence()
  fun getMoviePersistence(): MoviePersistence = database.moviePersistence()
}
