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

  private fun getWishList() = movies.filter { it.isInWishList }
  private fun getHistory() = movies.filter { it.isInHistory }

  private const val pageSize = 10

  override fun addToHistory(movie: MovieBriefUM) {
    val local = movies.find{ it.id == movie.id }
    if (local != null) local.isInHistory = true else movies.add(movie).also { movie.isInHistory = true }
  }

  override fun addToHistory(movie: MovieDetailedUM) {
    addToHistory(movie.toBrief())
  }

  override fun removeFromHistory(movie: MovieBriefUM) {
    movies.find{ it.id == movie.id }?.isInHistory = false
  }

  override fun removeFromHistory(movie: MovieDetailedUM) {
    removeFromHistory(movie.toBrief())
  }

  override fun addToWishList(movie: MovieBriefUM) {
    val local = movies.find{ it.id == movie.id }
    if (local != null) local.isInWishList = true else movies.add(movie).also { movie.isInWishList = true }
  }

  override fun addToWishList(movie: MovieDetailedUM) {
    addToWishList(movie.toBrief())
  }

  override fun removeFromWishList(movie: MovieBriefUM) {
    movies.find{ it.id == movie.id }?.isInWishList = false
  }

  override fun removeFromWishList(movie: MovieDetailedUM) {
    removeFromWishList(movie.toBrief())
  }

  override fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>> {
    return Observable.fromCallable {
      moviesToUpdate.apply {
        this.forEach { updatedMovie ->
          movies.find { m -> m.id == updatedMovie.id }?.let{
            updatedMovie.isInWishList = it.isInWishList
            updatedMovie.isInHistory = it.isInHistory
          }
        }
      }
    }
  }

  override fun getWishListPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>> {
    return paginatedObservable(nextPageIntent) { page -> getMoviesAtPage(page, getWishList()) }
  }

  override fun getHistoryPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>> {
    return paginatedObservable(nextPageIntent) { page -> getMoviesAtPage(page, getHistory()) }
  }

  /**
   * Заменить на реализацию с БД через PagedList. См. util.Pagination, класс PaginatedOnSubscribe
   * надо будет допилить для работы с PagedList по аналогии с PagedListWrapper
   */
  private fun getMoviesAtPage(page: Int, list: List<MovieBriefUM>): Single<PagedListWrapper<MovieBriefUM>> =
    Single.just(
      PagedListWrapper(
        page = page,
        total_results = list.size,
        total_pages = list.size / pageSize + 1,
        results = if (pageSize * page < list.size) list.subList(pageSize * (page - 1), pageSize * page).toMutableList()
        else list.subList(pageSize * (page - 1), list.size).toMutableList()

      )
    )

}
