package movie.history

import io.reactivex.Observable
import movie.common.AddToWishList
import movie.common.MovieListPresenter
import movie.common.MovieScreenView
import movie.common.MovieScreenVS
import movie.common.RemoveFromHistory
import movie.common.RemoveFromWishList
import movie.common.ScreenAction
import movie.common.UpdateMovieList
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
          UpdateMovieList(previousState.content.toMutableList().apply { removeAt(action.position) })
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
          UpdateMovieList(previousState.content.apply { this[action.position].isInWishList = false })
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

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>, refreshIntent: Observable<Unit>) =
    movieService.getHistoryPaged(nextPageIntent, refreshIntent)
}
