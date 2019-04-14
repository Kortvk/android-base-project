package ru.appkode.base.data.storage.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.entities.core.movie.MovieBriefSM
import storage.db.Converters


private const val DATABASE_VERSION = 1
const val DATABASE_NAME = "task.db"

@Database(
  entities = [MovieBriefSM::class],
  version = DATABASE_VERSION
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun moviePersistence(): MoviePersistence
}


const val TABLE_MOVIE = "movie"
@Dao
interface MoviePersistence {

  @Query("SELECT * FROM $TABLE_MOVIE ORDER BY sort ASC LIMIT 20*:page-20, 20*:page")
  fun getMovies(page: Int): Single<List<MovieBriefSM>>

  @Query("SELECT * FROM $TABLE_MOVIE WHERE id=:movieId")
  fun getMovie(movieId: Long): Observable<MovieBriefSM>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun addMovie(movie: MovieBriefSM)

  @Update
  fun updateMovie(movie: MovieBriefSM)

  @Delete
  fun deleteMovie(movie: MovieBriefSM)

  @Query("SELECT COUNT(*) FROM $TABLE_MOVIE")
  fun count(): Int

  @Query("DELETE FROM $TABLE_MOVIE")
  fun clearAll()
}