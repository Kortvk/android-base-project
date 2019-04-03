package ru.appkode.base.repository.movie

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.KeywordNM
import ru.appkode.base.entities.core.movie.MovieFilter
import ru.appkode.base.entities.core.movie.MovieNM

class RemoteMovieRepositoryImpl: RemoteMovieRepository {
  override fun getMovieById(id: Int): Observable<MovieNM> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun getPopularMovies(): Observable<List<MovieNM>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun filterMovies(filter: MovieFilter): Observable<List<MovieNM>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun searchKeywords(keyword: String): Observable<List<KeywordNM>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

}