package ru.appkode.base.ui.movie

import android.view.View
import androidx.core.view.isVisible
import io.reactivex.Observable
import kotlinx.android.synthetic.main.duck_list_controller.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class MovieListController : BaseMviController<MovieScreenViewState, MovieScreenView, MovieListPresenter>(),
  MovieScreenView {

  override fun initializeView(rootView: View) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun removeFromWishListIntent(): Observable<Int> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  override fun loadNextPageIntent(): Observable<Unit> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  protected val adapter = MovieListPagedAdapter()

  override fun createConfig(): BaseMviController.Config {
    return object : BaseMviController.Config {
      override val viewLayoutResource: Int
        get() = R.layout.duck_list_controller
    }
  }

  override fun renderViewState(viewState: MovieScreenViewState) {
    fieldChanged(viewState, { it.movieListViewState }) {
      duck_list_loading.isVisible = viewState.movieListViewState.isLoading
      duck_list_recycler.isVisible = viewState.movieListViewState.isContent
      duck_list_empty_list.isVisible = (viewState.movieListViewState.isContent &&
        viewState.movieListViewState.asContent().isEmpty())
      if (viewState.movieListViewState.isContent) adapter.items.addAll(viewState.movieListViewState.asContent())
    }
  }

  override fun createPresenter(): MovieListPresenter =
    MovieListPresenter(
      DefaultAppSchedulers,
      RepositoryHelper.getMovieRepository())
}