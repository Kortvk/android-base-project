package ru.appkode.base.data.storage

import android.content.Context
import android.util.Log
import androidx.room.Room
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.Action
import io.reactivex.functions.BiFunction
import ru.appkode.base.data.storage.db.AppDatabase
import ru.appkode.base.data.storage.db.DATABASE_NAME
import ru.appkode.base.data.storage.db.MoviePersistence
import ru.appkode.base.entities.core.movie.toStorageModel
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import java.util.*

object DatabaseHelper {

  private lateinit var database: AppDatabase

  fun createDatabase(context: Context) {
    if(!::database.isInitialized)
      database = Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME).build()
    /*Completable.fromAction{
      DatabaseHelper.getMoviePersistence().clearAll()
    }.subscribeOn(DefaultAppSchedulers.io)*/

    /*Single.just(DatabaseHelper.getMoviePersistence().count())
      .subscribeOn(DefaultAppSchedulers.io)
      .map { Log.d("current", "just " + it) }*/

    Log.d("current", "createDatabase")


    Observable.zip(
      Observable.fromCallable {
        DatabaseHelper.getMoviePersistence().count()
      }.subscribeOn(DefaultAppSchedulers.io),
      Observable.fromCallable {
        DatabaseHelper.getMoviePersistence().count()
      }.subscribeOn(DefaultAppSchedulers.io),
      BiFunction<Int, Int, Pair<Int, Int>> { t1, t2 -> Pair(t1, t2) }
    )
      .subscribe {
      Log.d("current", "Observable zip "+ it)}


    Observable.fromCallable {
      DatabaseHelper.getMoviePersistence().count()
    }.subscribeOn(DefaultAppSchedulers.io)
      .subscribe { item-> Log.d("current", "Observable "+item) }

    /*val res = Completable.fromAction{
      DatabaseHelper.getMoviePersistence().count()
    }.subscribeOn(DefaultAppSchedulers.io)
      .toSingle { Log.d("current", "just " + this) }
      .map { Log.d("current", "map single " + it) }*/



  }

  fun getMoviePersistence(): MoviePersistence = database.moviePersistence()
}
