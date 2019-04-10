package movie.local

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM

interface LocalMovieRepository {
  fun addToWishList(movie: MovieBriefUM)
  fun removeFromWishList(movie: MovieBriefUM)
  fun addToWishList(movie: MovieDetailedUM)
  fun removeFromWishList(movie: MovieDetailedUM)
  fun getWishListPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>>
}