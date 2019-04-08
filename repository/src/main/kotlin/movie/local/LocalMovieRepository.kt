package ru.appkode.base.repository.movie

import io.reactivex.Flowable
import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM


interface LocalMovieRepository {
  fun addToWishList(movie: MovieBriefUM)
  fun removeFromWishList(movie: MovieBriefUM)
  fun getWishListPaged(nextPageSignal: Observable<Unit>): Flowable<List<MovieBriefUM>>
  fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Flowable<List<MovieBriefUM>>
}