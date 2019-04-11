package movie.common

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import kotlinx.android.synthetic.main.controller_movie_list.*
import ru.appkode.base.ui.core.core.BaseMviController
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import movie.adapter.BasicMovieAdapter
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.movie.adapter.EVENT_ID_ADD_TO_WISHLIST_CLICKED
import ru.appkode.base.ui.movie.adapter.EVENT_ID_MORE_INFORMATION_CLICKED
import ru.appkode.base.ui.movie.adapter.EVENT_ID_OPEN_DETAILS

abstract class MovieListController(args: Bundle) :
  BaseMviController<MovieScreenViewState, MovieScreenView, MovieListPresenter>(args),
  MovieScreenView {

  constructor(id: Int) : this(Bundle().also { it.putInt("id", id) })

  private lateinit var adapter: BasicMovieAdapter

  override fun createConfig(): BaseMviController.Config {
    return object : BaseMviController.Config {
      override val viewLayoutResource: Int
        get() = ru.appkode.base.ui.R.layout.controller_movie_list
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
  }

  override fun elementSwipedLeft(): Observable<Int> {
    //TODO: Здесь должна быть логика преобразующая свайп в Observable по аналогии с кликами и прокруткой
    return Observable.just(999)
  }

  override fun elementSwipedRight(): Observable<Int> {
    //TODO: Здесь должна быть логика преобразующая свайп в Observable по аналогии с кликами и прокруткой
    return Observable.just(999)
  }

  /**
   * Интент, вызывающий onNext() каждый раз, когда пользователь нажимает на элемент списка
   * @return [Int] - позиция элемента в списке
   */
  override fun elementClicked(): Observable<Long> {
    return adapter.eventsRelay.filterEvents(EVENT_ID_OPEN_DETAILS)
  }

  /**
   * Интент, вызывающий onNext() каждый раз, когда пользователь меняет состояние чекбокса "в избранное"
   * какого-либо элемента списка
   * @return [Int] - позиция элемента в списке
   */
  override fun itemWishListStateChangeIntent(): Observable<Int> {
    return adapter.eventsRelay.filterEvents(EVENT_ID_ADD_TO_WISHLIST_CLICKED)
  }

  /**
   * Интент, вызывающий onNext() каждый раз, когда пользователь нажимает на кнопку разворачивания элемента
   * @return [Int] - позиция элемента в списке
   */
  override fun showMoreMovieInfoIntent(): Observable<Int> {
    return adapter.eventsRelay.filterEvents(EVENT_ID_MORE_INFORMATION_CLICKED)
  }

  /**
   * Интент, вызывающий onNext() каждый раз, когда RecyclerView отобразил последний элемент списка,
   * загруженного в адаптер
   */
  override fun loadNextPageIntent(): Observable<Unit> {
    return movie_list_recycler.scrollEvents().filter {
      (movie_list_recycler.layoutManager as LinearLayoutManager).let {
        (it.childCount + it.findFirstVisibleItemPosition() >= it.itemCount
            && it.findFirstVisibleItemPosition() >= 0
            && it.childCount >= PAGE_SIZE)
      }
    }.map { Unit }
  }

  override fun renderViewState(viewState: MovieScreenViewState) {
    fieldChanged(viewState, { it.state }) {
      movie_list_loading.isVisible = viewState.state.isLoading
      movie_list_recycler.isVisible = viewState.state.isContent
      movie_list_empty.isVisible = (viewState.state.isContent &&
          viewState.state.asContent().isEmpty())
      if (viewState.state.isContent
        && viewState.state.asContent() != previousViewState?.state?.content
      ) {
        adapter.items = viewState.state.asContent().toMutableList()
      }
    }
    fieldChanged(viewState, { it.singleStateChange }) {
      if (viewState.singleStateChange.first != null
        && viewState.singleStateChange.second != null
      ) {
        adapter.items[viewState.singleStateChange.first!!] = viewState.singleStateChange.second!!
        adapter.notifyItemChanged(viewState.singleStateChange.first!!)
        viewState.state.asContent()[viewState.singleStateChange.first!!].apply { isExpanded = !isExpanded }
      }
    }
  }
}