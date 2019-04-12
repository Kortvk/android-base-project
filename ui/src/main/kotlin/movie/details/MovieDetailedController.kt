package ru.appkode.base.ui.movie.details

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.jakewharton.rxbinding3.swiperefreshlayout.refreshes
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.controller_movie_detailed.*
import kotlinx.android.synthetic.main.layout_movie_rating.*
import movie.adapter.CastAdapter
import movie.navigation.DETAIL_SCREEN_ID_KEY
import ru.appkode.base.entities.core.movie.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.requireView
import ru.appkode.base.ui.movie.GENERATED_MARGINS

class MovieDetailedController(args: Bundle) :
  BaseMviController<MovieDetailedViewState, MovieDetailedView, MovieDetailedPresenter>(args),
  MovieDetailedView {

  private lateinit var adapter: CastAdapter

  override fun createConfig(): Config {
    return object : BaseMviController.Config {
      override val viewLayoutResource: Int
        get() = R.layout.controller_movie_detailed
    }
  }

  override fun refreshIntent(): Observable<Unit> = refresher.refreshes()

  override fun inWishListStateChangeIntent(): Observable<Unit> = checkbox_movie_fav.clicks()

  override fun inHistoryStateChangeIntent(): Observable<Unit> = checkbox_movie_history.clicks()

  override fun initializeView(rootView: View) {
    adapter = CastAdapter()
    recycler_cast.layoutManager =
      LinearLayoutManager(applicationContext).apply { this.orientation = LinearLayoutManager.HORIZONTAL }
    recycler_cast.adapter = adapter
  }

  override fun renderViewState(viewState: MovieDetailedViewState) {
    fieldChanged(viewState, { it.state }) {
      if (!viewState.state.isLoading) refresher.isRefreshing = false
      if (viewState.state.isError) showSnackbar(viewState.state.asError())
      if (viewState.state.isContent) bindItems(viewState.state.asContent())
    }
  }

  private fun bindItems(movie: MovieDetailedUM) {
    tv_movie_title.text = movie.title
    tv_movie_year.text = movie.releaseDate.substringBefore("-")
    tv_movie_runtime.text = requireView.context.getString(R.string.runtime, movie.runtime)
    movie_status.text = movie.status
    tv_movie_rating.text = movie.voteAverage.toString()
    Glide.with(refresher)
      .load(BASE_IMAGE_URL + IMAGE_PROFILE_SIZE + movie.posterPath).into(iv_movie_poster)
    tv_movie_description.text = movie.overview
    movie.keywords?.map { renderChipForKeyword(it) }?.forEach { keywords_group.addView(it) }
    tv_movie_tagline.text = movie.tagline
    Glide.with(refresher)
      .load(BASE_IMAGE_URL + IMAGE_BACKDROP_SIZE + movie.backdrop).into(toolbar_image)
    movie.cast?.let { adapter.items = it }
    movie.crew?.let {
      tv_directed_by.text =
        requireView.context.getString(R.string.directed, it.find { it.job == "Director" }?.name)
    }
    movie.crew?.let {
      tv_written_by.text =
        requireView.context.getString(R.string.directed, it.find { it.job == "Screenplay" }?.name)
    }
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
    MovieDetailedPresenter(DefaultAppSchedulers, RepositoryHelper.getMovieService(), args.getLong(DETAIL_SCREEN_ID_KEY))
}


