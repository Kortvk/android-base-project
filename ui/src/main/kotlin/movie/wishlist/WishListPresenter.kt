package ru.appkode.base.ui.movie.wishlist

import io.reactivex.Observable
import movie.common.*
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers

class WishListPresenter(
  schedulers: AppSchedulers,
  movieService: MovieService
) : MovieListPresenter(schedulers, movieService) {

  override fun processRemoveFromHistory(
    previousState: MovieScreenViewState,
    action: RemoveFromHistory
  ): Pair<MovieScreenViewState, Command<Observable<out ScreenAction>>?> {
    require(action.position < previousState.state.asContent().size)
    return previousState to command(
      movieService
        .removeFromHistory(previousState.state.asContent()[action.position]).doAction {
          UpdateSingleItem(action.position) { movie -> movie.apply { isInHistory = false } }
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
          UpdateSingleItem(action.position) { null }
        }
    )
  }

  override fun bindSwipeLeftIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft)
      .concatMap { Observable.just(AddToHistory(it), RemoveFromWishList(it)) }
  }

  override fun bindSwipeRightIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedRight).map { RemoveFromWishList(it) }
  }

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>, reloadIntent: Observable<Unit>) =
    movieService.getWishListPaged(nextPageIntent, reloadIntent)
}
