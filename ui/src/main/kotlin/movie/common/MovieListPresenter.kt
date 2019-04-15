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
object ShowLoading : ScreenAction()
object Undo : ScreenAction()
data class LoadNextPage(val content: List<MovieBriefUM>) : ScreenAction()
data class ShowError(val error: String) : ScreenAction()
data class ItemWishListStateChanged(val position: Int) : ScreenAction()
data class ItemHistoryStateChanged(val position: Int) : ScreenAction()
data class AddToWishList(val position: Int) : ScreenAction()
data class AddToHistory(val position: Int) : ScreenAction()
data class RemoveFromWishList(val position: Int) : ScreenAction()
data class RemoveFromHistory(val position: Int) : ScreenAction()
data class MoveToWishList(val position: Int) : ScreenAction()
data class MoveToHistory(val position: Int) : ScreenAction()
data class OpenDetails(val id: Long) : ScreenAction()
data class ExpandCollapseMovieItem(val position: Int) : ScreenAction()
data class UpdateSingleItem(
  val position: Int,
  val movie: MovieBriefUM,
  val isUndoable: Boolean = false,
  val description: String = ""
) : ScreenAction()

data class UpdateMovieList(
  val list: List<MovieBriefUM>,
  val isUndoable: Boolean = false,
  val description: String = ""
) : ScreenAction()

/**
 * Наследники этого презентера (презентеры поиска, вишлиста и истории) реализуют абстрактные методы,
 * чтобы забиндить свайп на нужную команду (придать свайпам влево-вправо разные значения на разных экранах)
 * Также необходимо реализовать кастомную логику обработки удаления элементов на разных экранах:
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
      intent(MovieScreenView::undoIntent).map { Undo },
      bindSwipeLeftIntent(), bindSwipeRightIntent(),
      getPagedMovieListSource(
        intent(MovieScreenView::loadNextPageIntent),
        intent(MovieScreenView::refreshIntent).startWith(Unit)
      )
        .switchMap {
          Observable.just<ScreenAction>(LoadNextPage(it)).startWith(ShowLoading)
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
      is UpdateMovieList -> processUpdateMovieList(previousState, action)
      is ItemWishListStateChanged -> processWishListStateChanged(previousState, action)
      is AddToHistory -> processAddToHistory(previousState, action)
      is RemoveFromHistory -> processRemoveFromHistory(previousState, action)
      is OpenDetails -> processOpenDetails(previousState, action)
      is ExpandCollapseMovieItem -> processExpandCollapseItem(previousState, action)
      is ItemHistoryStateChanged -> processItemHistoryStateChanged(previousState, action)
      is ShowError -> processShowError(previousState, action)
      is ShowLoading -> processShowLoading(previousState)
      is UpdateSingleItem -> processSingleItemUpdate(previousState, action)
      is Undo -> processUndo(previousState)
      is MoveToHistory -> processMoveToHistory(previousState, action)
      is MoveToWishList -> processMoveToWishList(previousState, action)
    }
  }

  private fun processMoveToHistory(
    previousState: MovieScreenVS,
    action: MoveToHistory
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to command(movieService
      .moveToHistory(previousState.content[action.position])
      .doAction {
        UpdateMovieList(previousState.content.toMutableList().apply {
          removeAt(action.position)
        }, true, "${previousState.content[action.position].title} Moved to History")
      })
  }

  private fun processMoveToWishList(
    previousState: MovieScreenVS,
    action: MoveToWishList
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to command(movieService
      .moveToWishlist(previousState.content[action.position])
      .doAction {
        UpdateMovieList(previousState.content.toMutableList().apply {
          removeAt(action.position)
        }, true, "${previousState.content[action.position].title} Moved to Wishlist")
      })
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
        UpdateSingleItem(
          position = action.position,
          movie = previousState.content[action.position].copy(isInWishList = true),
          isUndoable = true,
          description = "${previousState.content[action.position].title} added to Wishlist"
        )
      }
    )
  }

  private fun processAddToHistory(
    previousState: MovieScreenVS,
    action: AddToHistory
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to command(
      movieService.addToHistory(previousState.content[action.position]).doAction {
        UpdateSingleItem(
          position = action.position,
          movie = previousState.content[action.position].copy(isInHistory = true),
          isUndoable = true,
          description = "${previousState.content[action.position].title} added to history"
        )
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

  private fun processUpdateMovieList(
    previousState: MovieScreenVS,
    action: UpdateMovieList
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return if (action.isUndoable) {
      previousState.produceUndoableState(action.list, action.description) to null
    } else {
      MovieScreenVS.Content(action.list) to null
    }
  }

  private fun processSingleItemUpdate(
    previousState: MovieScreenVS,
    action: UpdateSingleItem
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return if (action.isUndoable)
      previousState.produceUndoableState(action.position, action.movie, action.description) to null
    else {
      val updatedContent = previousState.content.toMutableList().apply { this[action.position] = action.movie }
      MovieScreenVS.Content(updatedContent) to null
    }
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
      UpdateSingleItem(
        position = action.position,
        movie = previousState.content[action.position]
          .copy(isExpanded = !previousState.content[action.position].isExpanded)
      )
    )
  }

  private fun processUndo(previousState: MovieScreenVS)
    : Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to command(
      movieService.undoLastOperation().doAction { UpdateMovieList(previousState.contentSnapshot) }
    )
  }

  override fun createInitialState(): MovieScreenVS {
    return MovieScreenVS.Loading()
  }
}