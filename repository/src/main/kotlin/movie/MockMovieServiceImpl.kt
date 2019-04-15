package ru.appkode.base.repository.movie

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import movie.local.LocalMovieRepository
import movie.remote.RemoteMovieRepository
import ru.appkode.base.entities.core.movie.MovieBriefSM
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.entities.core.movie.toBrief
import ru.appkode.base.entities.core.movie.toStorageModel
import ru.appkode.base.entities.core.movie.toUiModel

class MockMovieServiceImpl(
  private val localRepository: LocalMovieRepository,
  private val remoteMovieRepository: RemoteMovieRepository
) : MovieService {
  override fun moveToWishList(movie: MovieBriefUM): Completable {
    lastOperation = MoveToHistory(movie.toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toStorageModel().apply {
        isInHistory = false
        isInWishList = true
      })
    }.subscribeOn(Schedulers.io())
  }

  override fun moveToHistory(movie: MovieBriefUM): Completable {
    lastOperation = MoveToWishList(movie.toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toStorageModel().apply {
        isInHistory = true
        isInWishList = false
      })
    }.subscribeOn(Schedulers.io())
  }

  private val genres by lazy {
    remoteMovieRepository.getGenres().blockingGet()
  }

  private lateinit var lastOperation: Operation

  override fun removeFromHistory(movie: MovieBriefUM): Completable {
    lastOperation = RemoveFromHistory(movie.toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toStorageModel().apply { isInHistory = false })
    }.subscribeOn(Schedulers.io())
  }

  override fun removeFromHistory(movie: MovieDetailedUM): Completable {
    lastOperation = RemoveFromHistory(movie.toBrief().toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toBrief().toStorageModel().apply { isInHistory = false })
    }.subscribeOn(Schedulers.io())
  }

  override fun addToHistory(movie: MovieBriefUM): Completable {
    lastOperation = AddToHistory(movie.toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toStorageModel().apply { isInHistory = true })
    }.subscribeOn(Schedulers.io())
  }

  override fun addToHistory(movie: MovieDetailedUM): Completable {
    lastOperation = AddToHistory(movie.toBrief().toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toBrief().toStorageModel().apply { isInHistory = true })
    }.subscribeOn(Schedulers.io())
  }

  override fun removeFromWishList(movie: MovieBriefUM): Completable {
    lastOperation = RemoveFromWishList(movie.toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toStorageModel().apply { isInWishList = false })
    }.subscribeOn(Schedulers.io())
  }

  override fun removeFromWishList(movie: MovieDetailedUM): Completable {
    lastOperation = RemoveFromWishList(movie.toBrief().toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toBrief().toStorageModel().apply { isInWishList = false })
    }.subscribeOn(Schedulers.io())
  }

  override fun addToWishList(movie: MovieBriefUM): Completable {
    lastOperation = AddToWishList(movie.toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toStorageModel().apply { isInWishList = true })
    }.subscribeOn(Schedulers.io())
  }

  override fun addToWishList(movie: MovieDetailedUM): Completable {
    lastOperation = AddToWishList(movie.toBrief().toStorageModel())
    return Completable.fromCallable {
      localRepository.persistMovie(movie.toBrief().toStorageModel().apply { isInWishList = true })
    }.subscribeOn(Schedulers.io())
  }

  override fun getWishListPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> {
    return localRepository.getWishListPaged(nextPageIntent, reloadIntent).subscribeOn(Schedulers.io())
  }

  override fun getHistoryPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> {
    return localRepository.getHistoryPaged(nextPageIntent, reloadIntent).subscribeOn(Schedulers.io())
  }

  override fun getMovieDetailed(id: Long): Observable<MovieDetailedUM> {
    return remoteMovieRepository.getMovieById(id).toObservable().map { it.toUiModel() }
      .switchMap { localRepository.getStatusUpdates(it) }
      .subscribeOn(Schedulers.io())
  }

  override fun undoLastOperation(): Completable {
    return Completable.fromCallable { lastOperation.undo() }
  }

  override fun getPopularMoviesPaged(
    nextPageIntent: Observable<Unit>,
    reloadIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>> =
    remoteMovieRepository.getPopularMoviesPaged(nextPageIntent, reloadIntent)
      .map { list -> list.map { it.toUiModel(genres) } }
      .switchMap { localRepository.getStatusUpdates(it) }
      .subscribeOn(Schedulers.io())


  abstract inner class Operation(val movie: MovieBriefSM) {
    fun undo() = localRepository.persistMovie(movie)
  }

  inner class AddToHistory(movie: MovieBriefSM) : Operation(movie)
  inner class AddToWishList(movie: MovieBriefSM) : Operation(movie)
  inner class RemoveFromHistory(movie: MovieBriefSM) : Operation(movie)
  inner class RemoveFromWishList(movie: MovieBriefSM) : Operation(movie)
  inner class MoveToWishList(movie: MovieBriefSM) : Operation(movie)
  inner class MoveToHistory(movie: MovieBriefSM) : Operation(movie)
}


