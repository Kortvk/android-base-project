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
    previousState: MovieScreenVS,
    action: RemoveFromHistory
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    require(action.position < previousState.content.size)
    return previousState to command(
      movieService
        .removeFromHistory(previousState.content[action.position]).doAction {
          UpdateSingleItem(
            position = action.position,
            movie = previousState.content[action.position].copy(isInHistory = false),
            isUndoable = true,
            description = "${previousState.content[action.position].title} Removed from history"
          )
        }
    )
  }

  override fun processRemoveFromWishList(
    previousState: MovieScreenVS,
    action: RemoveFromWishList
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    require(action.position < previousState.content.size)
    return previousState to command(
      movieService
        .removeFromWishList(previousState.content[action.position]).doAction {
          UpdateMovieList(previousState.content.toMutableList().apply {
            removeAt(action.position)
          }, true, "${previousState.content[action.position].title} Removed from Wishlist")
        }
    )
  }

  override fun bindSwipeLeftIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft).map { MoveToHistory(it) }
  }

  override fun bindSwipeRightIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedRight).map { RemoveFromWishList(it) }
  }

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>, refreshIntent: Observable<Unit>) =
    movieService.getWishListPaged(nextPageIntent, refreshIntent)
}
