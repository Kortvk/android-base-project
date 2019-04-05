package ru.appkode.base.repository.movie

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableEmitter
import io.reactivex.FlowableOnSubscribe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import ru.appkode.base.data.network.movie.MovieAPI
import ru.appkode.base.entities.core.common.PagedListWrapper
import ru.appkode.base.entities.core.movie.MovieFilter

class RemoteMovieRepositoryImpl(private val movieApi: MovieAPI) : RemoteMovieRepository {

  override fun getMovieById(id: Int) = movieApi.getMovieById(id)

  override fun getGenres() = movieApi.getGenres()

  override fun getPopularMoviesPaged(nextPageSignal: Observable<Unit>) =
    getPagedFlowable(nextPageSignal) { movieApi.getPopularMovies(it) }

  override fun filterMoviesPaged(filter: MovieFilter, nextPageSignal: Observable<Unit>) =
    getPagedFlowable(nextPageSignal) { movieApi.filterMoviesPaged(buildFilterParameters(filter), it) }

  override fun searchMoviesPaged(query: String, nextPageSignal: Observable<Unit>) =
    getPagedFlowable(nextPageSignal) { movieApi.searchMoviesPaged(query, it) }

  override fun searchKeywordsPaged(keyword: String, nextPageSignal: Observable<Unit>) =
  getPagedFlowable(nextPageSignal) { movieApi.searchKeywordsPaged(keyword, it) }

  override fun searchCastPaged(name: String, nextPageSignal: Observable<Unit>) =
    getPagedFlowable(nextPageSignal) { movieApi.searchCastPaged(name, it) }

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

  private fun <T> getPagedFlowable(nextPageSignal: Observable<Unit>, nextPageSupplier: (Int) -> Single<PagedListWrapper<T>>) =
    Flowable.create<List<T>>(PagedFlowableOnSubscribe(nextPageSignal, nextPageSupplier), BackpressureStrategy.DROP)

  inner class PagedFlowableOnSubscribe<T, V : PagedListWrapper<T>>(
    private val nextPageSignal: Observable<Unit>,
    private val nextPageSupplier: (Int) -> Single<V>
  ) : FlowableOnSubscribe<List<T>> {
    private var nextPage = 0
    private var maxPages = 1
    private lateinit var disposable: Disposable
    override fun subscribe(emitter: FlowableEmitter<List<T>>) {
      if (nextPage + 1 <= maxPages) {
        disposable = nextPageSignal.subscribe {
          nextPageSupplier.invoke(++nextPage)
            .doOnSuccess {
              maxPages = it.totalPages
              emitter.onNext(it.results)
            }
            .doOnError { emitter.onError(it) }
            .subscribe()
        }
      }
    }
  }
}