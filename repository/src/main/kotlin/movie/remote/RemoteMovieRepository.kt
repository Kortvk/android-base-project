package movie.remote

import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.entities.core.movie.CastNM
import ru.appkode.base.entities.core.movie.GenreNM
import ru.appkode.base.entities.core.movie.KeywordNM
import ru.appkode.base.entities.core.movie.MovieBriefNM
import ru.appkode.base.entities.core.movie.MovieFilter
import ru.appkode.base.entities.core.movie.MovieDetailedNM

interface RemoteMovieRepository {
  fun getMovieById(id: Long): Single<MovieDetailedNM>
  fun getGenres(): Single<List<GenreNM>>
  fun getPopularMoviesPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefNM>>
  fun filterMoviesPaged(filter: MovieFilter, nextPageIntent: Observable<Unit>): Observable<List<MovieBriefNM>>
  fun searchMoviesPaged(query: String, nextPageIntent: Observable<Unit>): Observable<List<MovieBriefNM>>
  fun searchKeywordsPaged(keyword: String, nextPageIntent: Observable<Unit>): Observable<List<KeywordNM>>
  fun searchCastPaged(name: String, nextPageIntent: Observable<Unit>): Observable<List<CastNM>>
}