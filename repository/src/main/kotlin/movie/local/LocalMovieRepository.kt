package movie.local

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import io.reactivex.Completable
import ru.appkode.base.entities.core.movie.MovieBriefSM

interface LocalMovieRepository {
  fun addToWishList(movie: MovieBriefUM)
  fun removeFromWishList(movie: MovieBriefUM)
  fun getWishListPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>>
}