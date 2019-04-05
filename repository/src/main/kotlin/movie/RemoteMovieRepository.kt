package ru.appkode.base.repository.movie

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.entities.core.movie.CastNM
import ru.appkode.base.entities.core.movie.GenreNM
import ru.appkode.base.entities.core.movie.KeywordNM
import ru.appkode.base.entities.core.movie.MovieBriefNM
import ru.appkode.base.entities.core.movie.MovieFilter
import ru.appkode.base.entities.core.movie.MovieDetailNM

interface RemoteMovieRepository {
  fun getMovieById(id: Int): Single<MovieDetailNM>
  fun getGenres(): Single<List<GenreNM>>
  fun getPopularMoviesPaged(nextPageSignal: Observable<Unit>): Flowable<List<MovieBriefNM>>
  fun filterMoviesPaged(filter: MovieFilter, nextPageSignal: Observable<Unit>): Flowable<List<MovieBriefNM>>
  fun searchMoviesPaged(query: String, nextPageSignal: Observable<Unit>): Flowable<List<MovieBriefNM>>
  fun searchKeywordsPaged(keyword: String, nextPageSignal: Observable<Unit>): Flowable<List<KeywordNM>>
  fun searchCastPaged(name: String, nextPageSignal: Observable<Unit>): Flowable<List<CastNM>>
}