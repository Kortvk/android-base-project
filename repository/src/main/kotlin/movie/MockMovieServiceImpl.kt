package ru.appkode.base.repository.movie

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import movie.local.LocalMovieRepository
import movie.remote.RemoteMovieRepository
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.entities.core.movie.toUiModel

class MockMovieServiceImpl(
  private val localRepository: LocalMovieRepository,
  private val remoteMovieRepository: RemoteMovieRepository
) : MovieService {

  private val genres by lazy {
    remoteMovieRepository.getGenres().blockingGet()
  }

  override fun removeFromHistory(movie: MovieBriefUM): Completable {
    return Completable.fromCallable { localRepository.removeFromHistory(movie) }.subscribeOn(Schedulers.io())
  }

  override fun removeFromHistory(movie: MovieDetailedUM): Completable {
    return Completable.fromCallable { localRepository.removeFromHistory(movie) }.subscribeOn(Schedulers.io())
  }

  override fun addToHistory(movie: MovieBriefUM): Completable {
    return Completable.fromCallable { localRepository.addToHistory(movie) }.subscribeOn(Schedulers.io())
  }

  override fun addToHistory(movie: MovieDetailedUM): Completable {
    return Completable.fromCallable { localRepository.addToHistory(movie) }.subscribeOn(Schedulers.io())
  }

  override fun removeFromWishList(movie: MovieBriefUM): Completable {
    return Completable.fromCallable { localRepository.removeFromWishList(movie) }.subscribeOn(Schedulers.io())
  }

  override fun removeFromWishList(movie: MovieDetailedUM): Completable {
    return Completable.fromCallable { localRepository.removeFromWishList(movie) }.subscribeOn(Schedulers.io())
  }

  override fun addToWishList(movie: MovieBriefUM): Completable {
    return Completable.fromCallable { localRepository.addToWishList(movie) }.subscribeOn(Schedulers.io())
  }

  override fun addToWishList(movie: MovieDetailedUM): Completable {
    Log.d("current", "addToWishList " + movie)
    return Completable.fromCallable { localRepository.addToWishList(movie) }.subscribeOn(Schedulers.io())
  }

  override fun getWishListPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> {
    return localRepository.getWishListPaged(nextPageIntent, reloadIntent).subscribeOn(Schedulers.io())
  }

  override fun getHistoryPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> {
    return localRepository.getHistoryPaged(nextPageIntent, reloadIntent).subscribeOn(Schedulers.io())
  }

  override fun getMovieDetailed(id: Long): Observable<MovieDetailedUM> {
    return remoteMovieRepository.getMovieById(id).toObservable().map { it.toUiModel() }.subscribeOn(Schedulers.io())
  }

  override fun getPopularMoviesPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> =
    remoteMovieRepository.getPopularMoviesPaged(nextPageIntent, reloadIntent)
      .map { list -> list.map { it.toUiModel(genres) } }
      //.switchMap { localRepository.getStatusUpdates(it) }
      .subscribeOn(Schedulers.io())
}