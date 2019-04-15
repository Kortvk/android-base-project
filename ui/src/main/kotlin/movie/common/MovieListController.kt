package movie.common

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import kotlinx.android.synthetic.main.controller_movie_list.*
import ru.appkode.base.ui.core.core.BaseMviController
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import movie.adapter.BasicMovieAdapter
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.core.core.util.requireView
import ru.appkode.base.ui.movie.adapter.EVENT_ID_ADD_TO_HISTORY_CLICKED
import ru.appkode.base.ui.movie.adapter.EVENT_ID_ADD_TO_WISHLIST_CLICKED
import ru.appkode.base.ui.movie.adapter.EVENT_ID_MORE_INFORMATION_CLICKED
import ru.appkode.base.ui.movie.adapter.EVENT_ID_OPEN_DETAILS
import ru.appkode.base.ui.movie.adapter.EVENT_ID_ITEM_SWIPED_LEFT
import ru.appkode.base.ui.movie.adapter.EVENT_ID_ITEM_SWIPED_RIGHT
import ru.appkode.base.ui.movie.adapter.EVENT_ID_UNDO
import java.util.concurrent.TimeUnit

abstract class MovieListController(args: Bundle) :
  BaseMviController<MovieScreenVS, MovieScreenView, MovieListPresenter>(args),
  MovieScreenView {

  private lateinit var adapter: BasicMovieAdapter

  abstract val emptyListMessage: String

  override fun createConfig(): BaseMviController.Config {
    return object : BaseMviController.Config {
      override val viewLayoutResource: Int
        get() = R.layout.controller_movie_list
    }
  }

  abstract fun getMovieListAdapter(): BasicMovieAdapter

  override fun initializeView(rootView: View) {
    adapter = getMovieListAdapter()
    adapter.setHasStableIds(true)

    val actionGuardManager = RecyclerViewTouchActionGuardManager()
    actionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
    actionGuardManager.isEnabled = true

    val dragDropManager = RecyclerViewDragDropManager()
    val swipeManager = RecyclerViewSwipeManager()
    val dragDropAdapter = dragDropManager.createWrappedAdapter(adapter.asRvAdapter())
    val swipeDragDropAdapter = swipeManager.createWrappedAdapter(dragDropAdapter)

    //dragnDropManager.setDraggingItemShadowDrawable(requireResources.getDrawable(R.drawable.material_shadow_z3) as NinePatchDrawable)
    val animator = DraggableItemAnimator()
    animator.supportsChangeAnimations = false

    movie_list_recycler.adapter = swipeDragDropAdapter
    movie_list_recycler.itemAnimator = animator
    movie_list_recycler.layoutManager = LinearLayoutManager(applicationContext)

    actionGuardManager.attachRecyclerView(movie_list_recycler)
    swipeManager.attachRecyclerView(movie_list_recycler)
    dragDropManager.attachRecyclerView(movie_list_recycler)

    tv_list_empty.text = emptyListMessage
  }

  override fun refreshIntent(): Observable<Unit> {
    return refresher.refreshes()
  }

  override fun elementSwipedLeft(): Observable<Int> {
    return adapter.eventsRelay.filterEvents(EVENT_ID_ITEM_SWIPED_LEFT)
  }

  override fun elementSwipedRight(): Observable<Int> {
    return adapter.eventsRelay.filterEvents(EVENT_ID_ITEM_SWIPED_RIGHT)
  }

  override fun elementClicked(): Observable<Long> {
    return adapter.eventsRelay.filterEvents<Long>(EVENT_ID_OPEN_DETAILS)
      .throttleFirst(500, TimeUnit.MILLISECONDS)
  }

  override fun itemHistoryStateChangeIntent(): Observable<Int> {
    return adapter.eventsRelay.filterEvents<Int>(EVENT_ID_ADD_TO_HISTORY_CLICKED)
      .throttleFirst(500, TimeUnit.MILLISECONDS)
  }

  override fun itemWishListStateChangeIntent(): Observable<Int> {
    return adapter.eventsRelay.filterEvents<Int>(EVENT_ID_ADD_TO_WISHLIST_CLICKED)
      .throttleFirst(500, TimeUnit.MILLISECONDS)
  }

  override fun showMoreMovieInfoIntent(): Observable<Int> {
    return adapter.eventsRelay.filterEvents(EVENT_ID_MORE_INFORMATION_CLICKED)
  }

  override fun undoIntent(): Observable<Unit> {
    return eventsRelay.filterEvents(EVENT_ID_UNDO)
  }

  override fun loadNextPageIntent(): Observable<Unit> {
    return movie_list_recycler.scrollEvents().throttleFirst(1, TimeUnit.SECONDS).filter {
      (movie_list_recycler.layoutManager as LinearLayoutManager).let { recycler ->
        (recycler.childCount + recycler.findFirstVisibleItemPosition() >= recycler.itemCount
          && recycler.findFirstVisibleItemPosition() >= 0
          && recycler.childCount >= PAGE_SIZE)
      }
    }.map { Unit }.throttleFirst(5, TimeUnit.SECONDS)
  }

  override fun renderViewState(viewState: MovieScreenVS) {
    fieldChanged(viewState, { it.state }) {
      if (viewState.state.isError) showSnackbar(viewState.state.asError())
      movie_list_recycler.isVisible = viewState.state.isContent
      refresher.post { refresher.isRefreshing = viewState.state.isLoading }
      movie_list_empty.isVisible = (viewState.state.isContent && viewState.content.isEmpty())

    }
    fieldChanged(viewState, { it.content }) {
      if (viewState.state.isContent) {
        adapter.items = viewState.content
      }
    }
    fieldChanged(viewState, { it.contentSnapshot }) {
      if (viewState.isUndoable) {
        showUndoSnackBar(viewState.lastOperationDescription)
      }
    }
  }

  private fun showUndoSnackBar(message: String) {
    Snackbar.make(requireView, message, Snackbar.LENGTH_LONG).setAction("Undo") {
      eventsRelay.accept(EVENT_ID_UNDO to Unit)
    }.show()
  }
}