package ru.appkode.base.ui.movie.filter

import android.os.Bundle
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import movie.adapter.BasicMovieAdapter
import movie.common.MovieListController
import movie.filter.FilterPresenter
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.movie.adapter.SwipeActions
import ru.appkode.base.ui.movie.adapter.SwipeControls

class FilterController(args: Bundle) : MovieListController(args) {

  override val emptyListMessage = "No movies returned"

  override fun getMovieListAdapter() = FilterMovieAdapter()

  override fun createPresenter() = FilterPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService())

}

class FilterMovieAdapter : BasicMovieAdapter(), SwipeControls {
  override fun delegateControlsAdapter(): BasicMovieAdapter = this
  override fun getSwipeAction(action: () -> Unit): SwipeResultAction = SwipeActions.DoNothing(action)
}