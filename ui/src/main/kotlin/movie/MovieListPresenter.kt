package ru.appkode.base.ui.movie

import io.reactivex.Flowable
import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers

sealed class ScreenAction
class LoadNextPage(val state: LceState<List<MovieBriefUM>>) : ScreenAction()
class ItemWishListStateChanged(val position: Int) : ScreenAction()
class AddToWishList(val position: Int) : ScreenAction()
class RemoveFromWishList(val position: Int) : ScreenAction()
class ModifyMovieList(val mutator: (List<MovieBriefUM>) -> List<MovieBriefUM>) : ScreenAction()
class AddToHistory(val position: Int): ScreenAction()
class RemoveFromHistory(val position: Int): ScreenAction()
class Ignore: ScreenAction()

abstract class MovieListPresenter(
  schedulers: AppSchedulers,
  protected val movieService: MovieService
) : BasePresenter<MovieScreenView, MovieScreenViewState, ScreenAction>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf<Observable<out ScreenAction>>(
      intent(MovieScreenView::itemWishListStateChangeIntent).map { ItemWishListStateChanged(it) },
      bindSwipeLeftIntent(), bindSwipeRightIntent(),
      getPagedMovieListSource(intent(MovieScreenView::loadNextPageIntent))
        .map { LoadNextPage(LceState.Content(it)) }.toObservable()
        .onErrorReturn { LoadNextPage(LceState.Error(it.message ?: "Unknown error")) }
    )
  }

  abstract fun bindSwipeLeftIntent(): Observable<out ScreenAction>

  abstract fun bindSwipeRightIntent(): Observable<out ScreenAction>
  /**
   * Источник данных, возвращающий список фильмов с паджинацией
   * @param nextPageIntent - Интент, генерирующий запросы на загрузку страницы из действий пользователя
   * @return следующая страница списка фильмов
   */
  abstract fun getPagedMovieListSource(nextPageIntent: Observable<Unit>): Flowable<List<MovieBriefUM>>

  override fun reduceViewState(
    previousState: MovieScreenViewState,
    action: ScreenAction
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    return when (action) {
      is RemoveFromWishList -> processRemoveItem(previousState, action)
      is AddToWishList -> processAddItem(previousState, action)
      is LoadNextPage -> processNextPage(previousState, action)
      is ModifyMovieList -> processModifyMovieList(previousState, action)
      is ItemWishListStateChanged -> processWishListStateChanged(previousState, action)
      is AddToHistory -> processAddToHistory(previousState, action)
      is RemoveFromHistory -> processRemoveFromHistory(previousState, action)
      else -> previousState to null
    }
  }

  abstract fun processAddToHistory(
    previousState: MovieScreenViewState,
    action: AddToHistory
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?>

  abstract fun processRemoveFromHistory(
    previousState: MovieScreenViewState,
    action: RemoveFromHistory
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?>

  private fun processNextPage(
    previousState: MovieScreenViewState,
    action: LoadNextPage
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    return if (previousState.state.isContent) {
      MovieScreenViewState(
        state =
        LceState.Content(previousState.state.asContent().toMutableList().also { it.addAll(action.state.asContent()) })
      ) to null
    } else
      MovieScreenViewState(state = action.state) to null
  }

  private fun processModifyMovieList(
    previousState: MovieScreenViewState,
    action: ModifyMovieList
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    return if (previousState.state.isContent && previousState.state.asContent().isNotEmpty()) {
      MovieScreenViewState(
        state = LceState.Content(action.mutator.invoke(previousState.state.asContent()))
      ) to null
    } else
      previousState to null
  }

  private fun processWishListStateChanged(
    previousState: MovieScreenViewState,
    action: ItemWishListStateChanged
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    return if (previousState.state.asContent()[action.position].isInWishList) {
      previousState to command(Observable.just(RemoveFromWishList(action.position) as ScreenAction))
    } else {
      previousState to command(Observable.just(AddToWishList(action.position) as ScreenAction))
    }
  }

  private fun processRemoveItem(
    previousState: MovieScreenViewState,
    action: RemoveFromWishList
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    return previousState to command(
      movieService.removeFromWishList(previousState.state.asContent()[action.position])
        .andThen(
          Observable.just(ModifyMovieList { list ->
            list.toMutableList()
              .also {
                it.removeAt(action.position)
              }
          } as ScreenAction)
        )
    )
  }

  private fun processAddItem(
    previousState: MovieScreenViewState,
    action: AddToWishList
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    return previousState.copy() to command(
      movieService.addToWishList(previousState.state.asContent()[action.position].also { it.isInWishList = true })
        .andThen(Observable.just(
          ModifyMovieList { list ->
            list.also {
              it[action.position].isInWishList = true
            }
          } as ScreenAction)
        )
    )
  }

  override fun createInitialState(): MovieScreenViewState {
    return MovieScreenViewState(
      state = LceState.Loading()
    )
  }


}