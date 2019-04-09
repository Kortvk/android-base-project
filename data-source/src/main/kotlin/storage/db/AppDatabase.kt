package ru.appkode.base.data.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.appkode.base.data.storage.persistence.task.MoviePersistence
import ru.appkode.base.data.storage.persistence.task.TaskPersistence
import ru.appkode.base.entities.core.movie.MovieBriefSM
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.task.TaskSM


private const val DATABASE_VERSION = 1
const val DATABASE_NAME = "task.db"

@Database(
  entities = [TaskSM::class, MovieBriefSM::class],
  version = DATABASE_VERSION
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun taskPersistence(): TaskPersistence
  abstract fun moviePersistence(): MoviePersistence
}
