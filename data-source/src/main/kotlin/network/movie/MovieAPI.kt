package ru.appkode.base.data.network.movie

import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.appkode.base.data.network.duck.GetDuckListResponse
import ru.appkode.base.entities.core.movie.KeywordNM
import ru.appkode.base.entities.core.movie.MovieNM

interface MovieAPI {
  @GET("/{id}")
  fun getMovieById(@Path("id") id: Int): Observable<MovieNM>

  @GET("/movie/popular")
  fun getPopularMovies(): Observable<List<MovieNM>>

  @GET("/movie/{id}?append_to_response=credits,images")
  fun getMoviesFiltered(@Path("id") id: Int, @QueryMap options: Map<String, String>): Observable<List<MovieNM>>

  @GET("/search/keyword")
  fun searchKeywords(@Query("query") name: String): Observable<List<KeywordNM>>

}