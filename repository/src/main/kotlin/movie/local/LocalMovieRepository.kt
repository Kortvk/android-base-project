package movie.local

import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefSM
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM

interface LocalMovieRepository {
  fun persistMovie(movie: MovieBriefSM)
  fun getWishListPaged(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getHistoryPaged(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>): Observable<List<MovieBriefUM>>
  fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>>
}