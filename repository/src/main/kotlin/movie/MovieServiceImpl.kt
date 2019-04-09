package ru.appkode.base.repository.movie

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import movie.local.LocalMovieRepository
import movie.remote.RemoteMovieRepository
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.entities.core.movie.toUiModel

class MovieServiceImpl(
  private val localRepository: LocalMovieRepository,
  private val remoteMovieRepository: RemoteMovieRepository
) : MovieService {

  private val genres = remoteMovieRepository.getGenres()
    .observeOn(Schedulers.io()).blockingGet()

  override fun removeFromWishList(movie: MovieBriefUM): Completable {
    return Completable.fromCallable { localRepository.removeFromWishList(movie) }
  }

  override fun addToWishList(movie: MovieBriefUM): Completable {
    return Completable.fromCallable { localRepository.addToWishList(movie) }
  }

  override fun getWishListPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>> {
    return localRepository.getWishListPaged(nextPageIntent)
  }

  override fun getMovieDetailed(id: Int): Observable<MovieDetailedUM> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getPopularMoviesPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>> =
    remoteMovieRepository.getPopularMoviesPaged(nextPageIntent)
      .map { list -> list.map { it.toUiModel(genres) } }
      .switchMap { localRepository.getStatusUpdates(it) }
}