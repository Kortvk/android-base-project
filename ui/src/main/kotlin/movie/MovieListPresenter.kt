package ru.appkode.base.ui.movie

import io.reactivex.Observable
import ru.appkode.base.repository.movie.RemoteMovieRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.duck.ScreenAction

class MovieListPresenter(
  schedulers: AppSchedulers,
  private val movieRepository: RemoteMovieRepository
) : BasePresenter<MovieListScreen.View, MovieListScreen.ViewState, ScreenAction>(schedulers) {
  override fun reduceViewState(
    previousState: MovieListScreen.ViewState,
    action: ScreenAction
  ): Pair<MovieListScreen.ViewState, Command<Observable<ScreenAction>>?> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createIntents(): List<Observable<out ScreenAction>> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun createInitialState(): MovieListScreen.ViewState {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }


}