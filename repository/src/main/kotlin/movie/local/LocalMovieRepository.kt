package movie.local

import io.reactivex.Flowable
import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM

interface LocalMovieRepository {
  fun addToWishList(movie: MovieBriefUM)
  fun removeFromWishList(movie: MovieBriefUM)
  fun getWishListPaged(nextPageIntent: Observable<Unit>): Flowable<List<MovieBriefUM>>
  fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Flowable<List<MovieBriefUM>>
}