package ru.appkode.base.repository.movie

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

  private val genres by lazy {remoteMovieRepository.getGenres().blockingGet() }

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
    return Completable.fromCallable { localRepository.addToWishList(movie) }.subscribeOn(Schedulers.io())
  }


  override fun getWishListPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>> {
    return localRepository.getWishListPaged(nextPageIntent).subscribeOn(Schedulers.io())
  }

  override fun getMovieDetailed(id: Int): Observable<MovieDetailedUM> {
    return remoteMovieRepository.getMovieById(id).toObservable().map { it.toUiModel() }.subscribeOn(Schedulers.io())
  }

  override fun getPopularMoviesPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>> =
    remoteMovieRepository.getPopularMoviesPaged(nextPageIntent)
      .map { list -> list.map { it.toUiModel(genres) } }
      .switchMap { localRepository.getStatusUpdates(it) }.subscribeOn(Schedulers.io())
}