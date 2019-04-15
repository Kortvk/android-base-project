package movie.common

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView
import ru.appkode.base.ui.core.core.ViewState

/**
 * Экран отображения фильмов
 */
interface MovieScreenView : MviView<MovieScreenVS> {

  /** Интент на обновление списка через SwipeRefresh */
  fun refreshIntent(): Observable<Unit>

  /**
   *  Интент на добавление элемента в Вишлист.
   *  @return [Int] - индекс элемента
   */
  fun itemWishListStateChangeIntent(): Observable<Int>

  /**
   *  Интент на добавление элемента в Историю.
   *  @return [Int] - индекс элемента
   */
  fun itemHistoryStateChangeIntent(): Observable<Int>

  /** Интент на загрузку следующей страницы Вишлиста */
  fun loadNextPageIntent(): Observable<Unit>

  /**
   *  Интент на клик по элементу.
   *  @return [Long] - id элемента
   */
  fun elementClicked(): Observable<Long>

  /**
   *  Интент на свайп элемента влево.
   * @return [Int] - индекс элемента
   */
  fun elementSwipedLeft(): Observable<Int>

  /**
   *  Интент на свайп элемента вправо.
   * @return [Int] - индекс элемента
   */
  fun elementSwipedRight(): Observable<Int>

  /**
   * Интент на сворачивание/разворачивание элемента списка.
   * @return [Int], индекс элемента
   */
  fun showMoreMovieInfoIntent(): Observable<Int>

  /** Интент на отмену последней операции. */
  fun undoIntent(): Observable<Unit>
}

@ViewState
data class MovieScreenVS(
  /** Копия предыдущей версии списка для отмены последнего действия */
  val contentSnapshot: List<MovieBriefUM> = emptyList(),
  val isUndoable: Boolean = false,
  val lastOperationDescription: String = "",
  val isHintVisible: Boolean = true,
  var content: List<MovieBriefUM> = emptyList(),
  val state: LceState<Unit>
) {

  /** Получить новое состояние на основе текущего, изменив единственный элемент */
  fun produceUndoableState(position: Int, movie: MovieBriefUM, description: String): MovieScreenVS {
    val updatedContent = content.toMutableList().apply { this[position] = movie }
    return UndoableContentUpdate(updatedContent, content.toList(), description)
  }

  /** Получить новое состояние на основе текущего, с обновленным списком */
  fun produceUndoableState(updatedContent: List<MovieBriefUM>, description: String): MovieScreenVS {
    return UndoableContentUpdate(updatedContent, content.toList(), description)
  }

  companion object {
    fun Content(content: List<MovieBriefUM>) = MovieScreenVS(content = content, state = LceState.Content(Unit))
    fun Loading() = MovieScreenVS(state = LceState.Loading(Unit))
    fun Error(error: String) = MovieScreenVS(state = LceState.Error(error))
    fun UndoableContentUpdate(updatedContent: List<MovieBriefUM>, snapshot: List<MovieBriefUM>, description: String) =
      MovieScreenVS(
        isUndoable = true,
        content = updatedContent,
        contentSnapshot = snapshot,
        state = LceState.Content(Unit),
        lastOperationDescription = description
      )
  }
}
