package ru.appkode.base.ui.movie

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import kotlinx.android.synthetic.main.movie_list_controller.*
import movie.adapter.EVENT_ID_ADD_TO_WISHLIST_CLICKED
import movie.adapter.MovieAdapter
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.filterEvents
import android.nfc.tech.MifareUltralight.PAGE_SIZE
import android.os.Bundle
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import movie.adapter.EVENT_ID_OPEN_DETAILS

abstract class MovieListController(args: Bundle) : BaseMviController<MovieScreenViewState, MovieScreenView, MovieListPresenter>(args),
  MovieScreenView {

  constructor(id: Int) : this(Bundle().also { it.putInt("id", id) })

  private lateinit var adapter: MovieAdapter

  override fun createConfig(): BaseMviController.Config {
    return object : BaseMviController.Config {
      override val viewLayoutResource: Int
        get() = R.layout.movie_list_controller
    }
  }

  override fun initializeView(rootView: View) {
    adapter = MovieAdapter()
    movie_list_recycler.layoutManager = LinearLayoutManager(applicationContext)
    movie_list_recycler.adapter = adapter
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
  override fun elementClicked(): Observable<Int> {
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
      if (viewState.state.isContent)
        adapter.items = viewState.state.asContent()
    }
  }
}