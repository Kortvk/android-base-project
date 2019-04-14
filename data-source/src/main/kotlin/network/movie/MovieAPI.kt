package ru.appkode.base.data.network.movie

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import ru.appkode.base.entities.core.common.PagedListWrapper
import ru.appkode.base.entities.core.movie.CastNM
import ru.appkode.base.entities.core.movie.GenreNM
import ru.appkode.base.entities.core.movie.KeywordNM
import ru.appkode.base.entities.core.movie.MovieBriefNM
import ru.appkode.base.entities.core.movie.MovieDetailedNM

interface MovieAPI {
  @GET("/3/movie/{id}?append_to_response=keywords,credits,images")
  fun getMovieById(@Path("id") id: Long): Single<MovieDetailedNM>

  @GET("/3/movie/popular")
  fun getPopularMovies(@Query("page") page: Int)
    : Single<PagedListWrapper<MovieBriefNM>>

  @GET("/3/discover/movie")
  fun filterMoviesPaged(
    @QueryMap options: Map<String, String>,
    @Query("page") page: Int
  )
    : Single<PagedListWrapper<MovieBriefNM>>

  @GET("/3/search/movie")
  fun searchMoviesPaged(
    @Query("query") title: String,
    @Query("page") page: Int
  )
    : Single<PagedListWrapper<MovieBriefNM>>

  @GET("/3/search/keyword")
  fun searchKeywordsPaged(
    @Query("query") name: String,
    @Query("page") page: Int
  )
    : Single<PagedListWrapper<KeywordNM>>

  @GET("/3/search/person")
  fun searchCastPaged(
    @Query("query") name: String,
    @Query("page") page: Int
  )
    : Single<PagedListWrapper<CastNM>>

  @GET("/3/genre/movie/list")
  fun getGenres(): Single<List<GenreNM>>
}