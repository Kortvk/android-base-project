package ru.appkode.base.ui.movie.filter

import android.os.Bundle
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants.REACTION_CAN_SWIPE_LEFT
import movie.adapter.BasicMovieAdapter
import movie.common.MovieListController
import movie.filter.FilterPresenter
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.movie.adapter.SwipeControls

class FilterController(args: Bundle): MovieListController(args) {

  override fun getMovieListAdapter() = FilterMovieAdapter()

  override fun createPresenter() = FilterPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService())
}

class FilterMovieAdapter : BasicMovieAdapter(), SwipeControls {
  override fun delegateControlsAdapter(): BasicMovieAdapter = this

  override fun onGetSwipeReactionType(holder: MovieNMViewHolder, position: Int, x: Int, y: Int): Int {
    return REACTION_CAN_SWIPE_LEFT
  }
}