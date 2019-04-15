package ru.appkode.base.repository.movie.local

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import movie.local.LocalMovieRepository
import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.entities.core.common.PagedListWrapper
import ru.appkode.base.entities.core.movie.MovieBriefSM
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.entities.core.movie.toBrief
import ru.appkode.base.entities.core.movie.toStorageModel
import ru.appkode.base.entities.core.movie.toUIModel
import ru.appkode.base.repository.util.paginatedObservable
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers


class LocalMovieRepositoryImpl : LocalMovieRepository {
  override fun persistMovie(movie: MovieBriefSM) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getHistoryPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  private val movies = mutableListOf<MovieBriefUM>()
  val pageSize = 10


  override fun getWishListPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> {
    return paginatedObservable(nextPageIntent, reloadIntent) { page ->
      getWishListPagedDb(page)
    }
  }


  fun getWishListPaged2(page: Int): Single<PagedListWrapper<MovieBriefUM>> {
    val res = Observable.zip(
      movies(page).toObservable(),
      Observable.fromCallable {
        DatabaseHelper.getMoviePersistence().count()
      }.subscribeOn(DefaultAppSchedulers.io),
      BiFunction<List<MovieBriefUM>, Int, PagedListWrapper<MovieBriefUM>> { t1, t2 ->
        PagedListWrapper(
          page,
          t2,
          t2 / pageSize,
          t1
        )
      }
    )
    return res.singleOrError()
  }


  fun getWishListPagedDb(page: Int): Single<PagedListWrapper<MovieBriefUM>> {
    Log.d("current", "getWishListPagedDb page = " + page)
    return DatabaseHelper.getMoviePersistence()
      .getMovies(page)
      .map { list -> PagedListWrapper<MovieBriefUM>(page, 10, 10, list.map { it.toUIModel() }) }
      //.map{ list -> list.map{it.toUIModel()}.map { list -> PagedListWrapper(page, 20, 1, list) } }
      .subscribeOn(DefaultAppSchedulers.io)
  }


  override fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>> {
    return movies().toObservable()
  }

  fun addMovie(movie: MovieBriefUM): Completable {
    return Completable.fromAction {
      DatabaseHelper.getMoviePersistence().addMovie(movie.toStorageModel())
    }.subscribeOn(DefaultAppSchedulers.io)
  }

  fun updateMovie(movie: MovieBriefUM): Completable {
    return Completable.fromAction {
      DatabaseHelper.getMoviePersistence().updateMovie(movie.toStorageModel())
    }.subscribeOn(DefaultAppSchedulers.io)
  }

  fun deleteMovie(movie: MovieBriefUM): Completable {
    return Completable.fromAction {
      DatabaseHelper.getMoviePersistence().deleteMovie(movie.toStorageModel())
    }.subscribeOn(DefaultAppSchedulers.io)
  }

  fun movie(movieId: Long): Observable<MovieBriefUM> {
    return DatabaseHelper.getMoviePersistence().getMovie(movieId).map { it.toUIModel() }
      .subscribeOn(DefaultAppSchedulers.io)
  }

  fun movies(page: Int = 1): Single<List<MovieBriefUM>> {
    return DatabaseHelper.getMoviePersistence().getMovies(page).map { list -> list.map { it.toUIModel() } }.subscribeOn(
      DefaultAppSchedulers.io
    )
  }
}
