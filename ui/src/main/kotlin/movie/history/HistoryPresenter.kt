package movie.history

import io.reactivex.Observable
import io.reactivex.ObservableSource
import movie.common.AddToWishList
import movie.common.MovieListPresenter
import movie.common.MovieScreenView
import movie.common.MovieScreenViewState
import movie.common.RemoveFromHistory
import movie.common.RemoveFromWishList
import movie.common.ScreenAction
import movie.common.UpdateSingleItem
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers

class HistoryPresenter(
  schedulers: AppSchedulers,
  movieService: MovieService
) : MovieListPresenter(schedulers, movieService) {

  override fun processRemoveFromHistory(
    previousState: MovieScreenViewState,
    action: RemoveFromHistory
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    return previousState to command(
      movieService
        .removeFromHistory(previousState.state.asContent()[action.position]).doAction {
          UpdateSingleItem(action.position) { null }
        }
    )
  }

  override fun processRemoveFromWishList(
    previousState: MovieScreenViewState,
    action: RemoveFromWishList
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    require(action.position < previousState.state.asContent().size)
    return previousState to command(
      movieService
        .removeFromWishList(previousState.state.asContent()[action.position]).doAction {
          UpdateSingleItem(action.position) { movie -> movie.apply { isInWishList = false } }
        }
    )
  }

  override fun bindSwipeLeftIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft).map { RemoveFromHistory(it) }
  }

  override fun bindSwipeRightIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedRight)
      .concatMap { Observable.just(AddToWishList(it), RemoveFromHistory(it)) }
  }

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>) =
    movieService.getHistoryPaged(nextPageIntent, reloadIntent)
}
