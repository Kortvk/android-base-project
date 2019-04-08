package ru.appkode.base.repository.movie

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM


interface MovieService {
  fun removeFromWishList(movie: MovieBriefUM): Completable
  fun addToWishList(movie: MovieBriefUM): Completable
  fun getWishListPaged(nextPageIntent: Observable<Unit>): Flowable<List<MovieBriefUM>>
  fun getMovieDetailed(id: Int): Observable<MovieDetailedUM>

  /*  ТОЛЬКО ДЛЯ ТЕСТОВ */
  fun getPopularMoviesPaged(nextPageIntent: Observable<Unit>): Flowable<List<MovieBriefUM>>
}