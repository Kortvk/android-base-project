package movie.common

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView
import ru.appkode.base.ui.core.core.ViewState
/**
 * Экран отображения фильмов
 */
interface MovieScreenView : MviView<MovieScreenViewState> {
  /**
   *  Интент на добавление элемента в Вишлист.
   *  @return [Observable], в котором каждый onNext() - position элемента, который нужно удалить
   */
  fun itemWishListStateChangeIntent(): Observable<Int>
  /**
   *  Интент на загрузку следующей страницы Вишлиста.
   *  @return [Observable], в котором каждый onNext() - интент запроса новой страницы
   */
  fun loadNextPageIntent(): Observable<Unit>
  /**
   *  Интент на свайп клик по элементу.
   *  @return [Observable], в котором каждый onNext() - интент клика по элементу
   */
  fun elementClicked(): Observable<Int>
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
data class MovieScreenViewState(
  /** Поле для изменения единственного элемента в листе - пара (Индекс, Измененный объект) */
  val singleStateChange: Pair<Int?, MovieBriefUM?> = null to null,
  val isHintVisible: Boolean = true,
  val state: LceState<List<MovieBriefUM>>
)