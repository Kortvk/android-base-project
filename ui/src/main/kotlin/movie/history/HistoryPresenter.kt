package movie.history

import io.reactivex.Observable
import movie.common.MoveToWishList
import movie.common.MovieListPresenter
import movie.common.MovieScreenView
import movie.common.MovieScreenVS
import movie.common.RemoveFromHistory
import movie.common.RemoveFromWishList
import movie.common.ScreenAction
import movie.common.UpdateMovieList
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
    previousState: MovieScreenVS,
    action: RemoveFromHistory
  ): Pair<MovieScreenVS, Command<Observable<out ScreenAction>>?> {
    return previousState to command(
      movieService
        .removeFromHistory(previousState.content[action.position]).doAction {
          UpdateMovieList(
            previousState.content.toMutableList().apply { removeAt(action.position) },
            true,
            "${previousState.content[action.position].title} Removed from history"
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
          UpdateSingleItem(
            position = action.position,
            movie = previousState.content[action.position].copy(isInWishList = false),
            isUndoable = true,
            description = "${previousState.content[action.position].title} Removed from Wishlist"
          )
        }
    )
  }

  override fun bindSwipeLeftIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft).map { RemoveFromHistory(it) }
  }

  override fun bindSwipeRightIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedRight).map { MoveToWishList(it) }
  }

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>, refreshIntent: Observable<Unit>) =
    movieService.getHistoryPaged(nextPageIntent, refreshIntent)
}
