package ru.appkode.base.ui.movie

import android.os.Bundle
import io.reactivex.Observable
import movie.navigation.DETAIL_SCREEN_ID_KEY
import movie.navigation.EVENT_ID_NAVIGATION_DETAILS
import movie.navigation.navigationEventsRelay
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.*
import ru.appkode.base.ui.core.core.util.AppSchedulers

sealed class ScreenAction
class LoadNextPage(val state: LceState<List<MovieBriefUM>>) : ScreenAction()
class ItemWishListStateChanged(val position: Int) : ScreenAction()
class AddToWishList(val position: Int) : ScreenAction()
class RemoveFromWishList(val position: Int) : ScreenAction()
class UpdateMovieList(val list: List<MovieBriefUM>) : ScreenAction()
class AddToHistory(val position: Int) : ScreenAction()
class RemoveFromHistory(val position: Int) : ScreenAction()
class OpenDetails(val id: Int) : ScreenAction()
class Error(val error: String) : ScreenAction()

abstract class MovieListPresenter(
  schedulers: AppSchedulers,
  protected val movieService: MovieService
) : BasePresenter<MovieScreenView, MovieScreenViewState, ScreenAction>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf<Observable<out ScreenAction>>(
      intent(MovieScreenView::itemWishListStateChangeIntent).map { ItemWishListStateChanged(it) },
      intent(MovieScreenView::elementClicked).map { OpenDetails(it) },
      bindSwipeLeftIntent(), bindSwipeRightIntent(),
      getPagedMovieListSource(intent(MovieScreenView::loadNextPageIntent))
        .map { LoadNextPage(LceState.Content(it)) }.doOnError { Error(it.message) }
    )
  }

  /**
   * Наследники этого презентера (презентеры поиска, вишлиста и истории) реализуют эти методы,
   * чтобы забиндить свайп на нужную команду (придать свайпам влево-вправо разные значения на разных экранах)
   */
  abstract fun bindSwipeLeftIntent(): Observable<out ScreenAction>

  abstract fun bindSwipeRightIntent(): Observable<out ScreenAction>
  /**
   * Источник данных, возвращающий список фильмов с паджинацией. На разных экранах это будут
   * различные методы сервиса - например, movieService.getHistoryPaged() на экране с историей
   * @param nextPageIntent - Интент, генерирующий запросы на загрузку страницы из действий пользователя
   * @return следующая страница списка фильмов
   */
  abstract fun getPagedMovieListSource(nextPageIntent: Observable<Unit>): Observable<List<MovieBriefUM>>

  override fun reduceViewState(
    previousState: MovieScreenViewState,
    action: ScreenAction
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    return when (action) {
      is RemoveFromWishList -> processRemoveFromWishList(previousState, action)
      is AddToWishList -> processAddToWishList(previousState, action)
      is LoadNextPage -> processNextPage(previousState, action)
      is UpdateMovieList -> processUpdateMovieList(action)
      is ItemWishListStateChanged -> processWishListStateChanged(previousState, action)
      is AddToHistory -> processAddToHistory(previousState, action)
      is RemoveFromHistory -> processRemoveFromHistory(previousState, action)
      is OpenDetails -> processOpenDetails(previousState, action)
      is Error -> processError(action)
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

  private fun processOpenDetails(previousState: MovieScreenViewState, action: OpenDetails)
      : Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    navigationEventsRelay.accept(EVENT_ID_NAVIGATION_DETAILS to Bundle().apply{ putInt(DETAIL_SCREEN_ID_KEY, action.id) })
    return previousState to null
  }

  private fun processError(action: Error)
      : Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    return MovieScreenViewState(state = LceState.Error(error = action.error)) to null
  }

  private fun processNextPage(
    previousState: MovieScreenViewState,
    action: LoadNextPage
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    return when (action.state.isContent) {
      true -> {
        val content = if (previousState.state.isContent) {
          previousState.state.asContent().toMutableList().apply { this.addAll(action.state.asContent()) }
        } else mutableListOf<MovieBriefUM>().apply { this.addAll(action.state.asContent()) }
        MovieScreenViewState(state = LceState.Content(content)) to null
      }
      false -> MovieScreenViewState(state = LceState.Loading()) to null
    }
  }

  private fun processUpdateMovieList(action: UpdateMovieList)
      : Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    return MovieScreenViewState(state = LceState.Content(action.list)) to null
  }

  private fun processWishListStateChanged(
    previousState: MovieScreenViewState,
    action: ItemWishListStateChanged
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    return when (previousState.state.asContent()[action.position].isInWishList) {
      true -> previousState to doAction(RemoveFromWishList(action.position))
      false -> previousState to doAction(AddToWishList(action.position))
    }
  }

  private fun processRemoveFromWishList(
    previousState: MovieScreenViewState,
    action: RemoveFromWishList
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    require(action.position < previousState.state.asContent().size)
    return previousState to command(
      movieService
        .removeFromWishList(previousState.state.asContent()[action.position]).doAction {
          UpdateMovieList(previousState.state.asContent().apply { this.toMutableList().removeAt(action.position) })
        }
    )
  }

  private fun processAddToWishList(
    previousState: MovieScreenViewState,
    action: AddToWishList
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    require(action.position < previousState.state.asContent().size)
    return previousState to command(
      movieService
        .addToWishList(previousState.state.asContent()[action.position]).doAction {
          UpdateMovieList(previousState.state.asContent().apply { this[action.position].isInWishList = true })
        }
    )
  }

  override fun createInitialState(): MovieScreenViewState {
    return MovieScreenViewState(
      state = LceState.Loading()
    )
  }
}