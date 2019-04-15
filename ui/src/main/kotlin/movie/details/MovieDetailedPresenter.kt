package ru.appkode.base.ui.movie.details

import io.reactivex.Observable
import ru.appkode.base.entities.core.movie.MovieDetailedUM
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.*
import ru.appkode.base.ui.core.core.util.AppSchedulers

sealed class ScreenAction
object AddToWishList : ScreenAction()
object RemoveFromWishList : ScreenAction()
object AddToHistory : ScreenAction()
object RemoveFromHistory : ScreenAction()
object ItemWishListStateChanged : ScreenAction()
object ItemHistoryStateChanged : ScreenAction()
object RefreshMovie : ScreenAction()
class LoadMovieDetails(val movie: MovieDetailedUM) : ScreenAction()

class MovieDetailedPresenter(
  schedulers: AppSchedulers,
  private val movieService: MovieService,
  private var movieId: Long
) : BasePresenter<MovieDetailedView, MovieDetailedViewState, ScreenAction>(schedulers) {

  override fun createIntents(): List<Observable<out ScreenAction>> {
    return listOf<Observable<out ScreenAction>>(
      intent(MovieDetailedView::inWishListStateChangeIntent).map { ItemWishListStateChanged },
      intent(MovieDetailedView::inHistoryStateChangeIntent).map { ItemHistoryStateChanged },
      intent(MovieDetailedView::refreshIntent).startWith(Unit).map { RefreshMovie }
    )
  }

  override fun createInitialState(): MovieDetailedViewState {
    return MovieDetailedViewState(state = LceState.Loading())
  }

  override fun reduceViewState(
    previousState: MovieDetailedViewState,
    action: ScreenAction
  ): Pair<MovieDetailedViewState, Command<Observable<out ScreenAction>>?> {
    return when (action) {
      is AddToWishList -> processAddToWishList(previousState)
      is RemoveFromWishList -> processRemoveFromWishList(previousState)
      is AddToHistory -> processAddToHistory(previousState)
      is RemoveFromHistory -> processRemoveFromHistory(previousState)
      is ItemWishListStateChanged -> processWishListStateChanged(previousState)
      is ItemHistoryStateChanged -> processHistoryStateChanged(previousState)
      is LoadMovieDetails -> processLoadMovieDetails(action)
      is RefreshMovie -> processRefreshMovie(previousState)
    }
  }

  private fun processWishListStateChanged(previousState: MovieDetailedViewState)
    : Pair<MovieDetailedViewState, Command<Observable<out ScreenAction>>?> {
    return if (previousState.state.asContent().isInWishList) {
      previousState to doAction(RemoveFromWishList)
    } else {
      previousState to doAction(AddToWishList)
    }
  }

  private fun processHistoryStateChanged(previousState: MovieDetailedViewState)
    : Pair<MovieDetailedViewState, Command<Observable<out ScreenAction>>?> {
    return if (previousState.state.asContent().isInHistory) {
      previousState to doAction(RemoveFromHistory)
    } else {
      previousState to doAction(AddToHistory)
    }
  }

  private fun processAddToWishList(previousState: MovieDetailedViewState)
    : Pair<MovieDetailedViewState, Command<Observable<out ScreenAction>>?> {
    return previousState to command(movieService.addToWishList(previousState.state.asContent())
      .doAction {
        LoadMovieDetails(previousState.state.asContent().copy(isInWishList = true))
      }
    )
  }

  private fun processRemoveFromWishList(previousState: MovieDetailedViewState)
    : Pair<MovieDetailedViewState, Command<Observable<out ScreenAction>>?> {
    return previousState to command(movieService.removeFromWishList(previousState.state.asContent())
      .doAction {
        LoadMovieDetails(previousState.state.asContent().copy(isInWishList = false))
      }
    )
  }

  private fun processAddToHistory(previousState: MovieDetailedViewState)
    : Pair<MovieDetailedViewState, Command<Observable<out ScreenAction>>?> {
    return previousState to command(movieService.addToHistory(previousState.state.asContent())
      .doAction {
        LoadMovieDetails(previousState.state.asContent().copy(isInHistory = true))
      }
    )
  }

  private fun processRemoveFromHistory(previousState: MovieDetailedViewState)
    : Pair<MovieDetailedViewState, Command<Observable<out ScreenAction>>?> {
    return previousState to command(movieService.removeFromHistory(previousState.state.asContent())
      .doAction {
        LoadMovieDetails(previousState.state.asContent().copy(isInHistory = false))
      }
    )
  }

  private fun processLoadMovieDetails(action: LoadMovieDetails)
    : Pair<MovieDetailedViewState, Command<Observable<ScreenAction>>?> {
    return MovieDetailedViewState(state = LceState.Content(action.movie)) to null
  }

  private fun processRefreshMovie(previousState: MovieDetailedViewState)
    : Pair<MovieDetailedViewState, Command<Observable<ScreenAction>>?> {
    return previousState to command(
      movieService.getMovieDetailed(movieId).doAction { LoadMovieDetails(it) }
    )
  }
}