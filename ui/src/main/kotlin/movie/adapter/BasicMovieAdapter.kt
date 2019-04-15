package movie.adapter

import android.graphics.drawable.RippleDrawable
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_movie_list.view.*
import kotlinx.android.synthetic.main.layout_movie_rating.view.*
import ru.appkode.base.entities.core.movie.BASE_IMAGE_URL
import ru.appkode.base.entities.core.movie.IMAGE_PROFILE_SIZE
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.ui.R
import ru.appkode.base.ui.movie.adapter.*
import kotlin.properties.Delegates

abstract class BasicMovieAdapter : RecyclerView.Adapter<BasicMovieAdapter.MovieVH>() {

  fun asRvAdapter() = this

  var items: MutableList<MovieBriefUM> by Delegates.observable(mutableListOf()) { _, _, _ ->
    notifyDataSetChanged()
  }

  val eventsRelay: PublishRelay<Pair<Int, Any>> = PublishRelay.create<Pair<Int, Any>>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieVH {
    return MovieVH(
      LayoutInflater.from(parent.context).inflate(R.layout.item_movie_list, parent, false)
    )
  }

  override fun getItemId(position: Int): Long = items[position].id

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: MovieVH, position: Int) = holder.bind(items[position])

  inner class MovieVH(view: View) : AbstractDraggableSwipeableItemViewHolder(view) {

    override fun getSwipeableContainerView(): View = itemView.layout_item_root

    private lateinit var movie: MovieBriefUM

    fun bind(movie: MovieBriefUM) {
      this.movie = movie
      itemView.in_history.isChecked = movie.isInHistory
      itemView.in_wish_list.isChecked = movie.isInWishList
      itemView.tv_title.text =
        itemView.context.getString(R.string.title_year, movie.title, movie.releaseDate.substringBefore("-"))
      itemView.in_wish_list.isChecked = movie.isInWishList
      val url = BASE_IMAGE_URL + IMAGE_PROFILE_SIZE + movie.poster
      Picasso.get().load(url).into(itemView.poster_image_view)
      itemView.tv_release_date.text = movie.releaseDate
      itemView.tv_genres.text = movie.genres.joinToString(separator = ", ")
      itemView.tv_movie_rating.text = movie.rating.toString()
      itemView.tv_overview.text = movie.overview
      itemView.votes_text_view.text = itemView.context.getString(R.string.votes, movie.votes)
      if (movie.isExpanded) expandView() else collapseView()
      bindIntents()
      rippleOnChecked()
    }

    private fun bindIntents() {
      itemView.in_wish_list.setOnClickListener {
        eventsRelay.accept(EVENT_ID_ADD_TO_WISHLIST_CLICKED to adapterPosition)
        eventsRelay.accept(EVENT_ADD_TO_WISH to movie)
      }
      itemView.layout_item_root.setOnClickListener {
        eventsRelay.accept(EVENT_ID_OPEN_DETAILS to movie.id)
      }
      itemView.more_information_check_box.setOnClickListener {
        eventsRelay.accept(EVENT_ID_MORE_INFORMATION_CLICKED to adapterPosition)
      }
      itemView.in_history.setOnClickListener {
        eventsRelay.accept(EVENT_ID_ADD_TO_HISTORY_CLICKED to adapterPosition)
      }
    }

    fun rippleOnChecked() {
      if (itemView.in_history.isChecked && itemView.in_history.background is RippleDrawable) {
        val rippleDrawable = itemView.in_history.background as RippleDrawable
        rippleDrawable.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        Handler().postDelayed({ rippleDrawable.state = intArrayOf() }, 200)
      }
      if (itemView.in_wish_list.isChecked && itemView.in_wish_list.background is RippleDrawable) {
        val rippleDrawable = itemView.in_wish_list.background as RippleDrawable
        rippleDrawable.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        Handler().postDelayed({ rippleDrawable.state = intArrayOf() }, 200)
      }
    }

    private fun expandView() = setDetailsVisibililty(View.VISIBLE)

    private fun collapseView() = setDetailsVisibililty(View.GONE)

    private fun setDetailsVisibililty(visibility: Int) {
      itemView.tv_overview.visibility = visibility
      itemView.votes_text_view.visibility = visibility
    }
  }
}

