package movie.local

import io.reactivex.*
import ru.appkode.base.entities.core.common.PagedListWrapper
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.entities.core.movie.toBrief
import ru.appkode.base.repository.util.paginatedObservable

/**
 * In-memory мокап локального репозитория с вишлистом и паджинацией
 */
object MockLocalMovieRepository : LocalMovieRepository {

  private val movies = mutableListOf<MovieBriefUM>()
  private const val pageSize = 10

  override fun addToWishList(movie: MovieBriefUM) {
    movies.add(movie).also{ movie.isInWishList = true }
  }

  override fun removeFromWishList(movie: MovieBriefUM) {
    movies.remove(movie)
  }

  override fun addToWishList(movie: MovieDetailedUM) {
    movies.add(movie.toBrief())
  }

  override fun removeFromWishList(movie: MovieDetailedUM) {
    movies.remove(movie.toBrief())
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
      Single.just(
      PagedListWrapper(
        page = page,
        total_results = movies.size,
        total_pages = movies.size / pageSize + 1,
        results = if (pageSize * page < movies.size) movies.subList(pageSize * (page - 1), pageSize * page).toMutableList()
        else movies.subList(pageSize * (page - 1), movies.size).toMutableList()

      )
    )

}
