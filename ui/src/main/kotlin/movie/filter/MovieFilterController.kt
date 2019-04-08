package ru.appkode.base.entities.core.movie.filter

import io.reactivex.Observable
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.repository.movie.MovieService
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.movie.MovieListController
import ru.appkode.base.ui.movie.MovieListPresenter

class MovieFilterController: MovieListController() {
  override fun createPresenter() =
    WishListPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService())
}
class WishListPresenter(schedulers: AppSchedulers,
                        movieService: MovieService
): MovieListPresenter(schedulers, movieService) {
  override fun getPagedMovieListSource(nextPageIntent: Observable<Unit>) = movieService.getPopularMoviesPaged(nextPageIntent)

}