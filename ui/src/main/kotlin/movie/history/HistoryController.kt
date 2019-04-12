package ru.appkode.base.ui.movie.history

import android.os.Bundle
import movie.adapter.BasicMovieAdapter
import movie.common.MovieListController
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.movie.adapter.DragAndDropControls
import ru.appkode.base.ui.movie.adapter.SwipeControls
import movie.history.HistoryPresenter
import ru.appkode.base.ui.core.core.BaseMviController

class HistoryController(args: Bundle) : MovieListController(args) {

  override val emptyListMessage = "History is empty"

  override fun getMovieListAdapter() = HistoryAdapter()

  override fun createPresenter() = HistoryPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService())

}

class HistoryAdapter : BasicMovieAdapter(), SwipeControls, DragAndDropControls {
  override fun delegateControlsAdapter(): BasicMovieAdapter = this
}
