package movie.common

import android.os.Bundle
import io.reactivex.Observable
import movie.navigation.DETAIL_SCREEN_ID_KEY
import movie.navigation.EVENT_ID_NAVIGATION_DETAILS
import movie.navigation.navigationEventsRelay
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.*
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers

sealed class ScreenAction
data class LoadNextPage(val state: LceState<List<MovieBriefUM>>) : ScreenAction()
data class ItemWishListStateChanged(val position: Int) : ScreenAction()
data class ItemHistoryStateChanged(val position: Int) : ScreenAction()
data class AddToWishList(val position: Int) : ScreenAction()
data class RemoveFromWishList(val position: Int) : ScreenAction()
data class UpdateMovieList(val list: List<MovieBriefUM>) : ScreenAction()
data class AddToHistory(val position: Int) : ScreenAction()
data class RemoveFromHistory(val position: Int) : ScreenAction()
data class OpenDetails(val id: Long) : ScreenAction()
data class ExpandCollapseMovieItem(val position: Int) : ScreenAction()
data class UpdateSingleItem(val position: Int, val mutator: (MovieBriefUM) -> MovieBriefUM?) : ScreenAction()

/**
 * Наследники этого презентера (презентеры поиска, вишлиста и истории) реализуют абстрактные методы,
 * чтобы забиндить свайп на нужную команду (придать свайпам влево-вправо разные значения на разных экранах)
 * Также для этого необходимо реализовать кастомную логику обработки удаления элементов на разных экранах:
 * Например, Фильтр дает запрос сервису на удаление фильма из вишлиста, но из списка элемент не удаляет.
 * Вишлист в свою очередь и делает запрос и удаляет элемент из списка
 */
abstract class MovieListPresenter(
  schedulers: AppSchedulers,
  protected val movieService: MovieService
) : BasePresenter<MovieScreenView, MovieScreenViewState, ScreenAction>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf<Observable<out ScreenAction>>(
      intent(MovieScreenView::itemWishListStateChangeIntent).map { ItemWishListStateChanged(it) },
      intent(MovieScreenView::itemHistoryStateChangeIntent).map { ItemHistoryStateChanged(it) },
      intent(MovieScreenView::elementClicked).map { OpenDetails(it) },
      intent(MovieScreenView::showMoreMovieInfoIntent).map { ExpandCollapseMovieItem(it) },
      bindSwipeLeftIntent(), bindSwipeRightIntent(),
      getPagedMovieListSource(intent(MovieScreenView::loadNextPageIntent), intent(MovieScreenView::refreshIntent))
        .doLceAction  { LoadNextPage(it) }
        .onErrorReturn {
          LoadNextPage(LceState.Error(it.message ?: "unknown error"))
        }
    )
  }

  /**
   * Источник данных, возвращающий список фильмов с паджинацией. На разных экранах это будут
   * различные методы сервиса - например, movieService.getHistoryPaged() на экране с историей
   * @param nextPageIntent - Интент, генерирующий запросы на загрузку страницы из действий пользователя
   * @return следующая страница списка фильмов
   */
  abstract fun getPagedMovieListSource(
    nextPageIntent: Observable<Unit>,
    refreshIntent: Observable<Unit>
  ): Observable<List<MovieBriefUM>>

  abstract fun bindSwipeLeftIntent(): Observable<out ScreenAction>

  abstract fun bindSwipeRightIntent(): Observable<out ScreenAction>

  abstract fun processRemoveFromHistory(
    previousState: MovieScreenViewState,
    action: RemoveFromHistory
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?>

  abstract fun processRemoveFromWishList(
    previousState: MovieScreenViewState,
    action: RemoveFromWishList
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?>

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
      is ExpandCollapseMovieItem -> processExpandCollapseItem(previousState, action)
      is UpdateSingleItem -> processSingleItemUpdate(previousState, action)
      is ItemHistoryStateChanged -> processItemHistoryStateChanged(previousState, action)
    }
  }

  private fun processAddToWishList(
    previousState: MovieScreenViewState,
    action: AddToWishList
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    require(action.position < previousState.state.asContent().size)
    return previousState to command(
      movieService.addToWishList(previousState.state.asContent()[action.position]).doAction {
        UpdateSingleItem(action.position) { movie -> movie.apply { isInWishList = true } }
      }
    )
  }

  private fun processAddToHistory(
    previousState: MovieScreenViewState,
    action: AddToHistory
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    require(action.position < previousState.state.asContent().size)
    return previousState to command(
      movieService.addToHistory(previousState.state.asContent()[action.position]).doAction {
        UpdateSingleItem(action.position) { movie -> movie.apply { isInHistory = true } }
      }
    )
  }

  private fun processOpenDetails(previousState: MovieScreenViewState, action: OpenDetails)
    : Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    navigationEventsRelay.accept(EVENT_ID_NAVIGATION_DETAILS to Bundle().apply {
      putLong(DETAIL_SCREEN_ID_KEY, action.id)
    })
    return previousState to null
  }

  private fun processNextPage(
    previousState: MovieScreenViewState,
    action: LoadNextPage
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> =
    if (action.state.isContent) {
      val content = if (previousState.state.isContent) {
        previousState.state.asContent().toMutableList().apply { this.addAll(action.state.asContent()) }
      } else mutableListOf<MovieBriefUM>().apply { this.addAll(action.state.asContent()) }
      MovieScreenViewState(state = LceState.Content(content)) to null
    } else MovieScreenViewState(state = action.state) to null

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

  private fun processItemHistoryStateChanged(
    previousState: MovieScreenViewState,
    action: ItemHistoryStateChanged
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    return when (previousState.state.asContent()[action.position].isInHistory) {
      true -> previousState to doAction(RemoveFromHistory(action.position))
      false -> previousState to doAction(AddToHistory(action.position))
    }
  }

  private fun processExpandCollapseItem(
    previousState: MovieScreenViewState,
    action: ExpandCollapseMovieItem
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    return previousState to doAction(
      UpdateSingleItem(action.position) { movie -> movie.apply { isExpanded = !isExpanded } }
    )
  }

  /**
   * копируем дата класс, выполняем над ним действие с мутабельными полями и сохраняем в состояние
   * для передачи в контроллер. Если вернем null, контроллер удлит элемент из списка.
   * Нужно, чтобы не копировать весь лист, поскольку в нем может быть Integer.MAX_VALUE записей
   */
  private fun processSingleItemUpdate(
    previousState: MovieScreenViewState,
    action: UpdateSingleItem
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    val movie = previousState.state.asContent()[action.position].copy()
    val singleItemChanges = action.position to action.mutator.invoke(movie)
    return previousState.copy(singleStateChange = singleItemChanges) to null
  }

  override fun createInitialState(): MovieScreenViewState {
    return MovieScreenViewState(
      state = LceState.Loading()
    )
  }
}