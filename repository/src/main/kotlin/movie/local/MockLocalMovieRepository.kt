package movie.local

import android.util.Log
import io.reactivex.*
import io.reactivex.functions.BiFunction
import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.entities.core.common.PagedListWrapper
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.entities.core.movie.toBrief
import ru.appkode.base.entities.core.movie.toStorageModel
import ru.appkode.base.entities.core.movie.toUIModel
import ru.appkode.base.repository.util.paginatedObservable
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

/**
 * In-memory мокап локального репозитория с вишлистом и паджинацией
 */
object MockLocalMovieRepository : LocalMovieRepository {

  private val movies = mutableListOf<MovieBriefUM>()

  private fun getWishList() = movies.filter { it.isInWishList }
  private fun getHistory() = movies.filter { it.isInHistory }

  private const val pageSize = 10

  override fun addToHistory(movie: MovieBriefUM) {
    val local = movies.find { it.id == movie.id }
    if (local != null) local.isInHistory = true else {
      val copy = movie.copy()
      movies.add(copy).also { copy.isInHistory = true }
    }
  }

  override fun addToHistory(movie: MovieDetailedUM) {
    addToHistory(movie.toBrief())
  }

  override fun removeFromHistory(movie: MovieBriefUM) {
    movies.find { it.id == movie.id }?.isInHistory = false
  }

  override fun removeFromHistory(movie: MovieDetailedUM) {
    removeFromHistory(movie.toBrief())
  }

  override fun addToWishList(movie: MovieBriefUM) {
    val local = movies.find { it.id == movie.id }
    if (local != null) local.isInWishList = true else {
      val copy = movie.copy()
      movies.add(copy).also { copy.isInWishList = true }
    }
  }

  override fun addToWishList(movie: MovieDetailedUM) {
    addToWishList(movie.toBrief())
  }

  override fun removeFromWishList(movie: MovieBriefUM) {
    movies.find { it.id == movie.id }?.isInWishList = false
  }

  override fun removeFromWishList(movie: MovieDetailedUM) {
    removeFromWishList(movie.toBrief())
  }

  override fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>> {
    return Observable.fromCallable {
      moviesToUpdate.apply {
        this.forEach { updatedMovie ->
          movies.find { m -> m.id == updatedMovie.id }?.let {
            updatedMovie.isInWishList = it.isInWishList
            updatedMovie.isInHistory = it.isInHistory
          }
        }
      }
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
  private fun getMoviesAtPage(page: Int, list: List<MovieBriefUM>): Single<PagedListWrapper<MovieBriefUM>> =
    Single.just(
      PagedListWrapper(
        page = page,
        total_results = list.size,
        total_pages = list.size / pageSize + 1,
        results = if (pageSize * page < list.size) list.subList(pageSize * (page - 1), pageSize * page).toList()
        else list.subList(pageSize * (page - 1), list.size).toList()

      )
    )

}




class MovieRepositoryImpl(): LocalMovieRepository {

  private val movies = mutableListOf<MovieBriefUM>()
  val pageSize = 10

  override fun addToWishList(movie: MovieDetailedUM) {
    addToWishList(movie.toBrief())
  }

  override fun removeFromWishList(movie: MovieDetailedUM) {
    removeFromWishList(movie.toBrief())
  }

  override fun addToWishList(movie: MovieBriefUM) {
    Log.d("current", "MovieRepositoryImpl addToWishList " + movie)
    //Completable.fromAction{
    movie.isInWishList = !movie.isInWishList
    if(!movie.isInWishList)
      DatabaseHelper.getMoviePersistence().deleteMovie(movie.toStorageModel())
    else
      DatabaseHelper.getMoviePersistence().addMovie(movie.toStorageModel())
    //}.subscribeOn(DefaultAppSchedulers.io)
  }

  override fun removeFromWishList(movie: MovieBriefUM) {
    Completable.fromAction{
      DatabaseHelper.getMoviePersistence().updateMovie(movie.toStorageModel())
    }.subscribeOn(DefaultAppSchedulers.io)
  }

  override fun getWishListPaged(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>> {
    return paginatedObservable(nextPageIntent) { page ->
      getWishListPagedDb(page)
    }
  }


  fun getWishListPaged2(page: Int): Single<PagedListWrapper<MovieBriefUM>>  {
    val res = Observable.zip(
      movies(page).toObservable(),
      Observable.fromCallable {
        DatabaseHelper.getMoviePersistence().count()
      }.subscribeOn(DefaultAppSchedulers.io),
      BiFunction<List<MovieBriefUM>, Int, PagedListWrapper<MovieBriefUM>>{t1, t2 -> PagedListWrapper(page, t2, t2/pageSize, t1)}
    )
    return res.singleOrError()
  }



  fun getWishListPagedDb(page: Int): Single<PagedListWrapper<MovieBriefUM>>  {
    Log.d("current", "getWishListPagedDb page = " + page)
    return DatabaseHelper.getMoviePersistence()
      .getMovies(page)
      .map{ list -> PagedListWrapper<MovieBriefUM>(page, 10, 10, list.map{it.toUIModel()}) }
      //.map{ list -> list.map{it.toUIModel()}.map { list -> PagedListWrapper(page, 20, 1, list) } }
      .subscribeOn(DefaultAppSchedulers.io)
  }


  override fun getStatusUpdates(moviesToUpdate: List<MovieBriefUM>): Observable<List<MovieBriefUM>> {
    return movies().toObservable()
  }

  fun addMovie(movie: MovieBriefUM): Completable {
    return Completable.fromAction{
      DatabaseHelper.getMoviePersistence().addMovie(movie.toStorageModel())
    }.subscribeOn(DefaultAppSchedulers.io)
  }

  fun updateMovie(movie: MovieBriefUM): Completable {
    return Completable.fromAction{
      DatabaseHelper.getMoviePersistence().updateMovie(movie.toStorageModel())
    }.subscribeOn(DefaultAppSchedulers.io)
  }

  fun deleteMovie(movie: MovieBriefUM): Completable {
    return Completable.fromAction{
      DatabaseHelper.getMoviePersistence().deleteMovie(movie.toStorageModel())
    }.subscribeOn(DefaultAppSchedulers.io)
  }

  fun movie(movieId: Long): Observable<MovieBriefUM> {
    return DatabaseHelper.getMoviePersistence().getMovie(movieId).map{ it.toUIModel() }.subscribeOn(DefaultAppSchedulers.io)
  }

  fun movies(page: Int = 1): Single<List<MovieBriefUM>> {
    return DatabaseHelper.getMoviePersistence().getMovies(page).map{ list -> list.map{it.toUIModel()} }.subscribeOn(DefaultAppSchedulers.io)
  }
}
