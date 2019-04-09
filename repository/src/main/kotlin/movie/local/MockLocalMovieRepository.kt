package movie.local

import io.reactivex.*
import ru.appkode.base.entities.core.common.PagedListWrapper
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.repository.util.paginatedObservable

/**
 * In-memory мокап локального репозитория с вишлистом и паджинацией
 */
object MockLocalMovieRepository : LocalMovieRepository {

  private val movies = mutableListOf<MovieBriefUM>()
  private const val pageSize = 10

  override fun addToWishList(movie: MovieBriefUM) {
    movies.add(movie)
  }

  override fun removeFromWishList(movie: MovieBriefUM) {
    movies.remove(movie)
  }

  override fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>> {
    return Observable.fromCallable {
      moviesToUpdate.apply {
        this.forEach { it.isInWishList = movies.find { m -> m.id == it.id }?.isInWishList ?: false }
      }
    }
  }

  override fun getWishListPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>> {
    return paginatedObservable(nextPageIntent) { page -> getMoviesAtPage(page) }
  }

  /**
   * Заменить на реализацию с БД через PagedList. См. util.Pagination, класс PaginatedOnSubscribe
   * надо будет допилить для работы с PagedList по аналогии с PagedListWrapper
   */
  private fun getMoviesAtPage(page: Int): Single<PagedListWrapper<MovieBriefUM>> =
    if (pageSize * page < movies.size) {
      Single.just(
        PagedListWrapper(
          page = page,
          total_results = movies.size,
          total_pages = movies.size / pageSize + 1,
          results = movies.subList(pageSize * (page - 1), pageSize).toMutableList()
        )
      )
    } else {
      Single.just(
        PagedListWrapper(
          page = 1,
          total_results = movies.size,
          total_pages = 1,
          results = movies.toMutableList()
        )
      )
    }
}
