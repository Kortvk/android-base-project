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
          UpdateMovieList(previousState.content.apply { this[action.position].isInHistory = false })
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
          })
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

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>, refreshIntent: Observable<Unit>) =
    movieService.getWishListPaged(nextPageIntent, refreshIntent)
}
