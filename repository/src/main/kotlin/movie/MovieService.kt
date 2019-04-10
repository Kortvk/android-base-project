package ru.appkode.base.repository.movie

import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM

interface MovieService {
  fun removeFromWishList(movie: MovieBriefUM): Completable
  fun removeFromWishList(movie: MovieDetailedUM): Completable
  fun addToWishList(movie: MovieBriefUM): Completable
  fun addToWishList(movie: MovieDetailedUM): Completable
  fun getWishListPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getMovieDetailed(id: Int): Observable<MovieDetailedUM>

  /*  ТОЛЬКО ДЛЯ ТЕСТОВ */
  fun getPopularMoviesPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
}