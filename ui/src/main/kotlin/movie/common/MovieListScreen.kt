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
  /**
   *  Интент на обновление списка.
   *  @return [Observable], в котором каждый onNext() - интент на swipRefresh
   */
  fun refreshIntent(): Observable<Unit>
  /**
   *  Интент на добавление элемента в Вишлист.
   *  @return [Observable], в котором каждый onNext() - position элемента, который нужно удалить
   */
  fun itemWishListStateChangeIntent(): Observable<Int>

  /**
   *  Интент на добавление элемента в Историю.
   *  @return [Observable], в котором каждый onNext() - position элемента, который нужно удалить
   */
  fun itemHistoryStateChangeIntent(): Observable<Int>

  /**
   *  Интент на загрузку следующей страницы Вишлиста.
   *  @return [Observable], в котором каждый onNext() - интент запроса новой страницы
   */
  fun loadNextPageIntent(): Observable<Unit>

  /**
   *  Интент на клик по элементу.
   *  @return [Observable], в котором каждый onNext() - интент клика по элементу
   */
  fun elementClicked(): Observable<Long>

  /**
   *  Интент на свайп элемента влево.
   *  @return [Observable], в котором каждый onNext() - интент свайпа влево
   */
  fun elementSwipedLeft(): Observable<Int>

  /**
   *  Интент на свайп элемента влево.
   *  @return [Observable], в котором каждый onNext() - интент свайпа вправо
   */
  fun elementSwipedRight(): Observable<Int>

  /**
  Интент на разворот элемента списка.
   */
  fun showMoreMovieInfoIntent(): Observable<Int>
}

@ViewState
data class MovieScreenVS(
  val singleStateChange: Pair<Int?, MovieBriefUM?> = null to null,
  val isHintVisible: Boolean = true,
  var content: List<MovieBriefUM> = emptyList(),
  val state: LceState<Unit>
) {
  companion object {
    fun Content(content: List<MovieBriefUM>) = MovieScreenVS(content = content, state = LceState.Content(Unit))
    fun Loading() = MovieScreenVS(state = LceState.Loading(Unit))
    fun Error(error: String) = MovieScreenVS(state = LceState.Error(error))
  }
}
