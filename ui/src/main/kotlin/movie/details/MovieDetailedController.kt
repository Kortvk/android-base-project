package ru.appkode.base.ui.movie.details

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.movie_detailed_controller.*
import kotlinx.android.synthetic.main.movie_list_controller.*
import ru.appkode.base.entities.core.movie.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.movie.GENERATED_MARGINS

class MovieDetailedController(args: Bundle) :
  BaseMviController<MovieDetailedViewState, MovieDetailedView, MovieDetailedPresenter>(args),
  MovieDetailedView {

  constructor(id: Int) : this(Bundle().also { it.putInt("id", id) })

  private lateinit var adapter: CastAdapter

  override fun createConfig(): Config {
    return object : BaseMviController.Config {
      override val viewLayoutResource: Int
        get() = R.layout.movie_detailed_controller
    }
  }

  override fun refreshIntent(): Observable<Unit> = refresher.refreshes()

  override fun inWishListStateChangeIntent(): Observable<Unit> = checkbox_movie_fav.clicks()

  override fun inHistoryStateChangeIntent(): Observable<Unit> = checkbox_movie_history.clicks()

  override fun initializeView(rootView: View) {
    adapter = CastAdapter()
    recycler_cast.layoutManager = LinearLayoutManager(applicationContext)
    recycler_cast.adapter = adapter
  }

  override fun renderViewState(viewState: MovieDetailedViewState) {
    fieldChanged(viewState, { it.state }) {
      //movie_list_loading.isVisible = viewState.state.isLoading
      // movie_list_recycler.isVisible = viewState.state.isContent
      if (viewState.state.isContent)
        bindItems(viewState.state.asContent())
    }
  }

  private fun bindItems(movie: MovieDetailedUM) {
    tv_movie_title.text = movie.title
    tv_movie_year.text = movie.releaseDate.substringBefore("-")
    tv_movie_runtime.text = view!!.context.getString(R.string.runtime, movie.runtime)
    movie_status.text = movie.status
    tv_movie_rating.text = movie.voteAverage.toString()
    Glide.with(refresher)
      .load(BASE_IMAGE_URL + IMAGE_SIZE_MEDIUM + movie.posterPath).into(iv_movie_poster)
    tv_movie_description.text = movie.overview
    movie.keywords?.map { renderChipForKeyword(it) }?.forEach { keywords_group.addView(it) }
    tv_movie_tagline.text = movie.tagline
    Glide.with(refresher)
      .load(BASE_IMAGE_URL + IMAGE_SIZE_SMALL + movie.backdrop).into(toolbar_image)
  }

  private fun renderChipForKeyword(text: String) = Chip(view!!.context).also {
    it.text = text
    val layoutParams = ViewGroup.MarginLayoutParams(
      ViewGroup.LayoutParams.WRAP_CONTENT,
      ViewGroup.LayoutParams.WRAP_CONTENT
    )
    layoutParams.bottomMargin = GENERATED_MARGINS
    it.layoutParams = layoutParams
    it.setTextAppearanceResource(R.style.ChipTextStyle)
  }

  override fun createPresenter(): MovieDetailedPresenter =
    MovieDetailedPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService(), args.getInt("id"))

}

