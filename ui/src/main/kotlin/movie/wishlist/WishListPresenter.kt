package ru.appkode.base.ui.movie.wishlist

import android.os.Bundle
import io.reactivex.Observable
import movie.common.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class WishListController(args: Bundle): MovieListController(args) {
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
    return previousState to null
  }

  override fun processRemoveFromHistory(
    previousState: MovieScreenViewState,
    action: RemoveFromHistory
  ): Pair<MovieScreenViewState, Command<Observable<ScreenAction>>?> {
    //TODO: здесь написать логику удаления из истории (см. по аналогии с вишлистом в MovieListPresenter)
    return previousState to null
  }

  override fun bindSwipeLeftIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft).filter { it != 999 }.map { AddToHistory(it) }
  }

  override fun bindSwipeRightIntent(): Observable<out ScreenAction> {
    return intent(MovieScreenView::elementSwipedLeft).filter { it != 999 }.map { RemoveFromWishList(it) }
  }

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>) = movieService.getWishListPaged(nextPageIntent)
}