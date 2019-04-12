package ru.appkode.base.ui.movie.details

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView
import ru.appkode.base.ui.core.core.ViewState
/**
 * Экран отображения фильмов
 */
interface MovieDetailedView : MviView<MovieDetailedViewState> {
  /**
   *  Интент на добавление элемента в Вишлист.
   *  @return [Observable], в котором каждый onNext() - position элемента, который нужно удалить
   */
  fun inWishListStateChangeIntent(): Observable<Unit>
  /**
   *  Интент на добавление элемента в Историю.
   *  @return [Observable], в котором каждый onNext() - position элемента, который нужно удалить
   */
  fun inHistoryStateChangeIntent(): Observable<Unit>
  /**
   *  Интент на обновление экрана.
   *  @return [Observable], в котором каждый onNext() - событие обновления через SwipeRefresh
   */
  fun refreshIntent(): Observable<Unit>
}

@ViewState
data class MovieDetailedViewState(
  val isHintVisible: Boolean = true,
  val state: LceState<MovieDetailedUM>
)