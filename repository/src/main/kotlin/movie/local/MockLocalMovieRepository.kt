package movie.local

import io.reactivex.*
import io.reactivex.Observable
import ru.appkode.base.entities.core.common.PagedListWrapper
import ru.appkode.base.entities.core.movie.MovieBriefSM
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.entities.core.movie.toUIModel
import ru.appkode.base.repository.util.paginatedObservable

/**
 * In-memory мокап локального репозитория с вишлистом и паджинацией
 */
object MockLocalMovieRepository : LocalMovieRepository {

  private val movies = mutableMapOf<Long, MovieBriefSM>()

  private fun getWishList() = movies.values.filter { it.isInWishList }
  private fun getHistory() = movies.values.filter { it.isInHistory }

  private const val pageSize = 10

  override fun persistMovie(movie: MovieBriefSM) {
    movies[movie.id] = movie
  }

  override fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>> {
    return Observable.fromCallable {
      moviesToUpdate.toMutableList().map { updatedMovie ->
        val localCopy = movies.values.find { m -> m.id == updatedMovie.id }
        if (localCopy != null)
          updatedMovie.copy(isInWishList = localCopy.isInWishList, isInHistory = localCopy.isInHistory)
        else updatedMovie
      }
    }
  }

  override fun getStatusUpdates(movieToUpdate: MovieDetailedUM): Observable<MovieDetailedUM> {
    return Observable.fromCallable {
      val localCopy = movies.values.find { m -> m.id == movieToUpdate.id }
      if (localCopy != null)
        movieToUpdate.copy(isInWishList = localCopy.isInWishList, isInHistory = localCopy.isInHistory)
      else movieToUpdate
    }
  }

  override fun getWishListPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> {
    return paginatedObservable(nextPageIntent, reloadIntent) { page -> getMoviesAtPage(page, getWishList()) }
  }

  override fun getHistoryPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> {
    return paginatedObservable(nextPageIntent, reloadIntent) { page -> getMoviesAtPage(page, getHistory()) }
  }

  /**
   * Заменить на реализацию с БД через PagedList. См. util.Pagination, класс PaginatedOnSubscribe
   * надо будет допилить для работы с PagedList по аналогии с PagedListWrapper
   */
  private fun getMoviesAtPage(page: Int, db: List<MovieBriefSM>): Single<PagedListWrapper<MovieBriefUM>> {
    val list = db.toList().map { it.toUIModel() }
    return Single.just(
      PagedListWrapper(
        page = page,
        total_results = list.size,
        total_pages = list.size / pageSize + 1,
        results = if (pageSize * page < list.size) list.subList(pageSize * (page - 1), pageSize * page).toList()
        else list.subList(pageSize * (page - 1), list.size).toList()
      )
    )
  }
}

