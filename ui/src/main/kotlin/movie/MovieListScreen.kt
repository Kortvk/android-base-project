package ru.appkode.base.ui.movie

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
   *  Интент на удаление элемента из Вишлиста.
   *  @return [Observable], в котором каждый onNext() - id элемента, который нужно удалить
   */
  fun removeFromWishListIntent(): Observable<Int>
  /**
   *  Интента на загрузку следующей страницы Вишлиста.
   *  Вызывается, когда пользователь долистал до конца текущей страницы
   *  @return [Observable], в котором каждый onNext() - интент запроса новой страницы
   */
  fun loadNextPageIntent(): Observable<Unit>
}

@ViewState
data class MovieScreenViewState(
  val isHintVisible: Boolean = true,
  val movieListViewState: LceState<List<MovieBriefUM>>
)