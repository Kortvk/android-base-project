package movie.filter

import android.os.Bundle
import io.reactivex.Observable
import movie.common.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class FilterController(args: Bundle): MovieListController(args) {
  override fun createPresenter() =
    FilterPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService())
}

class FilterPresenter(schedulers: AppSchedulers,
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
    return intent(MovieScreenView::elementSwipedLeft).filter { it != 999 }.map { AddToWishList(it) }
  }

  override fun bindSwipeRightIntent(): Observable<out ScreenAction?> {
    return intent(MovieScreenView::elementSwipedLeft).filter { it != 999 }.map { null }
  }

  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>) =
    movieService.getPopularMoviesPaged(nextPageIntent)
}
