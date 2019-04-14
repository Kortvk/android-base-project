package movie.filter

import io.reactivex.Observable
import movie.common.*
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers

class FilterPresenter(
  schedulers: AppSchedulers,
  movieService: MovieService
) : MovieListPresenter(schedulers, movieService) {

  override fun processRemoveFromWishList(
    previousState: MovieScreenVS,
    action: RemoveFromWishList
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to null
  }

  override fun processRemoveFromHistory(
    previousState: MovieScreenVS,
    action: RemoveFromHistory
  ): Pair<MovieScreenVS, Command<Observable<ScreenAction>>?> {
    return previousState to null
  }

  override fun bindSwipeLeftIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft).map { AddToHistory(it) }
  }

  override fun bindSwipeRightIntent(): Observable<out ScreenAction?> {
    return intent(MovieScreenView::elementSwipedRight).map { AddToWishList(it) }
  }

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>) =
    movieService.getPopularMoviesPaged(nextPageIntent, reloadIntent)
}
