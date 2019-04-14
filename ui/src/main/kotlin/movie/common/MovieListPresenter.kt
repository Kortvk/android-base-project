package movie.common

import android.os.Bundle
import android.util.Log
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
data class LoadNextPage(val content: List<MovieBriefUM>) : ScreenAction()
class ShowLoading : ScreenAction()
data class ShowError(val error: String) : ScreenAction()
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
) : BasePresenter<MovieScreenView, MovieScreenVS, ScreenAction>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf<Observable<out ScreenAction>>(
      intent(MovieScreenView::itemWishListStateChangeIntent).map { ItemWishListStateChanged(it) },
      intent(MovieScreenView::itemHistoryStateChangeIntent).map { ItemHistoryStateChanged(it) },
      intent(MovieScreenView::elementClicked).map { OpenDetails(it) },
      intent(MovieScreenView::showMoreMovieInfoIntent).map { ExpandCollapseMovieItem(it) },
      bindSwipeLeftIntent(), bindSwipeRightIntent(),
      getPagedMovieListSource(
        intent(MovieScreenView::loadNextPageIntent),
        intent(MovieScreenView::refreshIntent).startWith(Unit)
      )
        .switchMap {
          Observable.just<ScreenAction>(LoadNextPage(it)).startWith(ShowLoading())
        }
        .onErrorReturn { ShowError(it.message ?: "unknown error") }
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
    previousState: MovieScreenVS,
    action: RemoveFromHistory
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?>

  abstract fun processRemoveFromWishList(
    previousState: MovieScreenVS,
    action: RemoveFromWishList
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?>

  override fun reduceViewState(
    previousState: MovieScreenVS,
    action: ScreenAction
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
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
      is ItemHistoryStateChanged -> processItemHistoryStateChanged(previousState, action)
      is ShowError -> processShowError(previousState, action)
      is ShowLoading -> processShowLoading(previousState)
      is UpdateSingleItem -> processSingleItemUpdate(previousState, action)
    }
  }

  private fun processShowError(
    previousState: MovieScreenVS,
    action: ShowError
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState.copy(state = LceState.Error(action.error)) to null
  }

  private fun processShowLoading(previousState: MovieScreenVS)
    : Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState.copy(state = LceState.Loading()) to null
  }

  private fun processAddToWishList(
    previousState: MovieScreenVS,
    action: AddToWishList
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to command(
      movieService.addToWishList(previousState.content[action.position]).doAction {
        UpdateSingleItem(action.position) { movie -> movie.apply { isInWishList = true } }
      }
    )
  }

  private fun processAddToHistory(
    previousState: MovieScreenVS,
    action: AddToHistory
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to command(
      movieService.addToHistory(previousState.content[action.position]).doAction {
        UpdateSingleItem(action.position) { movie -> movie.apply { isInHistory = true } }
      }
    )
  }

  private fun processOpenDetails(previousState: MovieScreenVS, action: OpenDetails)
    : Pair<MovieScreenVS, Command<Observable<ScreenAction>>?> {
    navigationEventsRelay.accept(EVENT_ID_NAVIGATION_DETAILS to Bundle().apply {
      putLong(DETAIL_SCREEN_ID_KEY, action.id)
    })
    return previousState to null
  }

  private fun processNextPage(
    previousState: MovieScreenVS,
    action: LoadNextPage
  ): Pair<MovieScreenVS, Command<Observable<ScreenAction>>?> {
    return if (previousState.content.isNotEmpty()) {
      MovieScreenVS.Content(content = previousState.content.toMutableList()
        .apply {
          addAll(action.content.filter { newMovie ->
            !previousState.content.map { it.id }.contains(newMovie.id)
          })
        }) to null
    } else MovieScreenVS.Content(action.content) to null
  }

  private fun processUpdateMovieList(action: UpdateMovieList)
    : Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return MovieScreenVS.Content(action.list) to null
  }

  private fun processWishListStateChanged(
    previousState: MovieScreenVS,
    action: ItemWishListStateChanged
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return if (previousState.content[action.position].isInWishList) {
      previousState to doAction(RemoveFromWishList(action.position))
    } else previousState to doAction(AddToWishList(action.position))
  }

  private fun processItemHistoryStateChanged(
    previousState: MovieScreenVS,
    action: ItemHistoryStateChanged
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return if (previousState.content[action.position].isInHistory) {
      previousState to doAction(RemoveFromHistory(action.position))
    } else previousState to doAction(AddToHistory(action.position))
  }

  private fun processExpandCollapseItem(
    previousState: MovieScreenVS,
    action: ExpandCollapseMovieItem
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to doAction(
      UpdateSingleItem(action.position) { movie -> movie.apply { isExpanded = !isExpanded } }
    )
  }

  /**
   * копируем дата класс, выполняем над ним действие с мутабельными полями и сохраняем в состояние
   * для передачи в контроллер. Нужно, чтобы не копировать весь лист
   */
  private fun processSingleItemUpdate(
    previousState: MovieScreenVS,
    action: UpdateSingleItem
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    val movie = previousState.content[action.position].copy()
    val singleItemChanges = action.position to action.mutator.invoke(movie)
    action.mutator.invoke(previousState.content[action.position])
    return previousState.copy(singleStateChange = singleItemChanges) to null
  }

  override fun createInitialState(): MovieScreenVS {
    return MovieScreenVS.Loading()
  }


  fun processAddToFavorite(movie: MovieBriefUM) =
    movieService.addToWishList(movie)
}