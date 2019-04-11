package movie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.picasso.Picasso
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.item_movie_list.view.*
import kotlinx.android.synthetic.main.layout_movie_rating.view.*
import ru.appkode.base.entities.core.movie.BASE_IMAGE_URL
import ru.appkode.base.entities.core.movie.IMAGE_PROFILE_SIZE
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.ui.R
import ru.appkode.base.ui.movie.adapter.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

abstract class BasicMovieAdapter : RecyclerView.Adapter<BasicMovieAdapter.MovieNMViewHolder>() {

  fun asRvAdapter() = this

  var items: MutableList<MovieBriefUM> by Delegates.observable(mutableListOf()) { _, _, _ ->
    notifyDataSetChanged()
  }

  val eventsRelay: PublishRelay<Pair<Int, Any>> = PublishRelay.create<Pair<Int, Any>>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieNMViewHolder {
    return MovieNMViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_movie_list, parent, false)
    )
  }

  override fun getItemId(position: Int): Long = items[position].id

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: MovieNMViewHolder, position: Int) = holder.bind(items[position])

  inner class MovieNMViewHolder(view: View) : AbstractDraggableSwipeableItemViewHolder(view) {

    override fun getSwipeableContainerView(): View = itemView.layout_item_root

    private lateinit var movie: MovieBriefUM
    private lateinit var disposable: CompositeDisposable

    fun bind(movie: MovieBriefUM) {
      this.movie = movie
      if (movie.isInWishList) itemView.in_wish_list.isChecked = true
      itemView.movie_title_text_view.text = movie.title
      itemView.in_wish_list.isChecked = movie.isInWishList
      val url = BASE_IMAGE_URL + IMAGE_PROFILE_SIZE + movie.poster
      Picasso.get().load(url).into(itemView.poster_image_view)
      itemView.release_date_text_view.text = movie.releaseDate
      itemView.movie_genre_text_view.text = movie.genres.joinToString(separator = ", ")
      itemView.tv_movie_rating.text = movie.rating.toString()
      itemView.description_text_view.text = movie.overview
      itemView.votes_text_view.text = itemView.context.getString(R.string.votes, movie.votes)
      if (movie.isExpanded) expandView() else collapseView()
      bindIntents()
    }

    private fun bindIntents() {
      itemView.in_wish_list.setOnClickListener {
        eventsRelay.accept(EVENT_ID_ADD_TO_WISHLIST_CLICKED to adapterPosition)
      }
      itemView.poster_image_view.setOnClickListener {
        eventsRelay.accept(EVENT_ID_OPEN_DETAILS to movie.id)
      }
      itemView.more_information_check_box.setOnClickListener {
        eventsRelay.accept(EVENT_ID_MORE_INFORMATION_CLICKED to adapterPosition)
      }
    }

    private fun expandView() = setDetailsVisibililty(View.VISIBLE)

    private fun collapseView() = setDetailsVisibililty(View.GONE)

    private fun setDetailsVisibililty(visibility: Int) {
      itemView.description_text_view.visibility = visibility
      itemView.votes_text_view.visibility = visibility
    }
  }
}

