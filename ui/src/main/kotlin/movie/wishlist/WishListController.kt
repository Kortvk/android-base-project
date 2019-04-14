package ru.appkode.base.ui.movie.wishlist

import android.os.Bundle
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import movie.adapter.BasicMovieAdapter
import movie.common.MovieListController
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.movie.adapter.DragAndDropControls
import ru.appkode.base.ui.movie.adapter.SwipeActions
import ru.appkode.base.ui.movie.adapter.SwipeControls

class WishListController(args: Bundle) : MovieListController(args) {

  override val emptyListMessage = "Wishlist is empty"

  override fun getMovieListAdapter() = WishListMovieAdapter()

  override fun createPresenter() = WishListPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService())

}

class WishListMovieAdapter : BasicMovieAdapter(), SwipeControls, DragAndDropControls {
  override fun getSwipeAction(action: () -> Unit): SwipeResultAction = SwipeActions.Remove(action)
  override fun delegateControlsAdapter(): BasicMovieAdapter = this
}