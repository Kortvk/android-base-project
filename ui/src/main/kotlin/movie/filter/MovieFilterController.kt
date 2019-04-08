package ru.appkode.base.entities.core.movie.filter

import io.reactivex.Observable
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.movie.*

class MovieFilterController: MovieListController() {



  override fun createPresenter() =
    WishListPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService())
}

class WishListPresenter(schedulers: AppSchedulers,
                        movieService: MovieService
): MovieListPresenter(schedulers, movieService) {

  override fun processAddToHistory(
    previousState: MovieScreenViewState,
    action: AddToHistory
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    //TODO: здесь написать логику добавления в историю (см. по аналогии с вишлистом в MovieListPresenter)
    TODO("not implemented")
  }

  override fun processRemoveFromHistory(
    previousState: MovieScreenViewState,
    action: RemoveFromHistory
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    //TODO: здесь написать логику удаления из истории (см. по аналогии с вишлистом в MovieListPresenter)
    TODO("not implemented")
  }

  override fun bindSwipeLeftIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft).map { AddToWishList(it) }
  }

  override fun bindSwipeRightIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft).map { Ignore() }
  }

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>) = movieService.getPopularMoviesPaged(nextPageIntent)

}