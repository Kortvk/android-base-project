package movie.local

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM

interface LocalMovieRepository {
  fun addToWishList(movie: MovieBriefUM)
  fun addToWishList(movie: MovieDetailedUM)
  fun removeFromWishList(movie: MovieBriefUM)
  fun removeFromWishList(movie: MovieDetailedUM)
  fun addToHistory(movie: MovieBriefUM)
  fun addToHistory(movie: MovieDetailedUM)
  fun removeFromHistory(movie: MovieBriefUM)
  fun removeFromHistory(movie: MovieDetailedUM)
  fun getWishListPaged(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getHistoryPaged(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>>
}