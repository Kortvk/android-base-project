package ru.appkode.base.repository.movie

import io.reactivex.*
import io.reactivex.disposables.Disposable
import ru.appkode.base.entities.core.movie.MovieBriefUM

object MockLocalMovieRepository : LocalMovieRepository {

  private val movies = mutableListOf<MovieBriefUM>()
  private const val pageSize = 10

  override fun addToWishList(movie: MovieBriefUM) {
    movies.add(movie)
  }

  override fun removeFromWishList(movie: MovieBriefUM) {
    movies.remove(movie)
  }

  override fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Flowable<List<MovieBriefUM>> {
    return Flowable.fromCallable {
      moviesToUpdate.apply {
        this.forEach { it.isInWishList = movies.find { m -> m.id == it.id }?.isInWishList ?: false }
      }
    }
  }

  override fun getWishListPaged(nextPageSignal: Observable<Unit>): Flowable<List<MovieBriefUM>> {
    return getPagedFlowable(nextPageSignal) { getMoviesAtPage(it) }
  }

  private fun getMoviesAtPage(page: Int): Single<List<MovieBriefUM>> =
    if (pageSize * page < movies.size) {
      Single.just(
        movies.subList(pageSize * (page - 1), pageSize).toMutableList()
      )
    } else {
      Single.just(
        movies.toMutableList()
      )
    }

  private fun <T> getPagedFlowable(
    nextPageSignal: Observable<Unit>,
    nextPageSupplier: (Int) -> Single<List<T>>
  ) =
    Flowable.create<List<T>>(PagedFlowableOnSubscribe(nextPageSignal, nextPageSupplier), BackpressureStrategy.DROP)

  class PagedFlowableOnSubscribe<T, V : List<T>>(
    private val nextPageSignal: Observable<Unit>,
    private val nextPageSupplier: (Int) -> Single<V>
  ) : FlowableOnSubscribe<List<T>> {
    private var nextPage = 0
    private var maxPages = 10
    private lateinit var disposable: Disposable
    override fun subscribe(emitter: FlowableEmitter<List<T>>) {
      nextPageSupplier.invoke(1).doOnSuccess { emitter.onNext(it) }.subscribe()
        disposable = nextPageSignal.subscribe {
          nextPageSupplier.invoke(++nextPage)
            .doOnSuccess {
              maxPages = if (it.size < pageSize) 1 else (it.size / pageSize + 1)
              if (nextPage + 1 <= maxPages) emitter.onNext(it)
            }
            .doOnError { emitter.onError(it) }
            .subscribe()
        }
    }
  }
}