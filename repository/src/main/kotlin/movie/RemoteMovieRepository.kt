package ru.appkode.base.repository.movie

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.KeywordNM
import ru.appkode.base.entities.core.movie.MovieFilter
import ru.appkode.base.entities.core.movie.MovieNM

interface RemoteMovieRepository {
  fun getMovieById(id: Int): Observable<MovieNM>
  fun getPopularMovies(): Observable<List<MovieNM>>
  fun filterMovies(filter: MovieFilter): Observable<List<MovieNM>>
  fun searchKeywords(keyword: String): Observable<List<KeywordNM>>
}