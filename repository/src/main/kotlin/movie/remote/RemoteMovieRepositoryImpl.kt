package movie.remote

import io.reactivex.Observable
import ru.appkode.base.data.network.movie.MovieAPI
import ru.appkode.base.entities.core.movie.MovieFilter
import ru.appkode.base.repository.util.paginatedObservable

class RemoteMovieRepositoryImpl(private val movieApi: MovieAPI) : RemoteMovieRepository {

  override fun getMovieById(id: Long) = movieApi.getMovieById(id)

  override fun getGenres() = movieApi.getGenres()

  override fun getPopularMoviesPaged(nextPageIntent: Observable<Unit>) =
    paginatedObservable(nextPageIntent) { page -> movieApi.getPopularMovies(page) }

  override fun filterMoviesPaged(filter: MovieFilter, nextPageIntent: Observable<Unit>) =
    paginatedObservable(nextPageIntent) { page -> movieApi.filterMoviesPaged(buildFilterParameters(filter), page) }

  override fun searchMoviesPaged(query: String, nextPageIntent: Observable<Unit>) =
    paginatedObservable(nextPageIntent) { page -> movieApi.searchMoviesPaged(query, page) }

  override fun searchKeywordsPaged(keyword: String, nextPageIntent: Observable<Unit>) =
    paginatedObservable(nextPageIntent) { page -> movieApi.searchKeywordsPaged(keyword, page) }

  override fun searchCastPaged(name: String, nextPageIntent: Observable<Unit>) =
    paginatedObservable(nextPageIntent) { page -> movieApi.searchCastPaged(name, page) }

  private fun buildFilterParameters(filter: MovieFilter): Map<String, String> {
    val queryMap = mutableMapOf<String, String>()
    filter.releaseDate?.let {
      queryMap["release_date.gte"] = it.start.toString()
      queryMap["release_date.lte"] = it.endInclusive.toString()
    }
    filter.rating?.let {
      queryMap["vote_average.gte"] = it.start.toString()
      queryMap["vote_average.lte"] = it.endInclusive.toString()
    }
    filter.cast?.let { queryMap["with_cast"] = it.joinToString(separator = ",") }
    filter.genres?.let { queryMap["with_genres"] = it.joinToString(separator = ",") }
    filter.keywords?.let { queryMap["with_keywords"] = it.joinToString(separator = ",") }
    return queryMap
  }
}
