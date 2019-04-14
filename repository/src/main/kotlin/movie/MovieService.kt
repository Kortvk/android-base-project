package ru.appkode.base.repository.movie

import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM

interface MovieService {
  fun removeFromWishList(movie: MovieBriefUM): Completable
  fun removeFromWishList(movie: MovieDetailedUM): Completable
  fun removeFromHistory(movie: MovieBriefUM): Completable
  fun removeFromHistory(movie: MovieDetailedUM): Completable
  fun addToWishList(movie: MovieBriefUM): Completable
  fun addToWishList(movie: MovieDetailedUM): Completable
  fun addToHistory(movie: MovieBriefUM): Completable
  fun addToHistory(movie: MovieDetailedUM): Completable
  fun getWishListPaged(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getHistoryPaged(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getMovieDetailed(id: Long): Observable<MovieDetailedUM>

  /**  ТОЛЬКО ДЛЯ ТЕСТОВ */
  fun getPopularMoviesPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>>
}