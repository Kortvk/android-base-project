package movie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieNMViewHolder>() {

  var items: MutableList<MovieBriefUM> by Delegates.observable(mutableListOf()) { _, _, _ ->
    notifyDataSetChanged()
  }

  val eventsRelay: PublishRelay<Pair<Int, Any>> = PublishRelay.create<Pair<Int, Any>>()

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieNMViewHolder {
    return MovieNMViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_movie_list, parent, false)
    )
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: MovieNMViewHolder, position: Int) = holder.bind(items[position])

  override fun onViewRecycled(holder: MovieNMViewHolder) = holder.unbind()

  inner class MovieNMViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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

    fun unbind() = disposable.dispose()

    private fun bindIntents() {
      disposable = CompositeDisposable()
      disposable.addAll(
        itemView.in_wish_list.clicks().throttleFirst(500, TimeUnit.MILLISECONDS).subscribe {
          eventsRelay.accept(EVENT_ID_ADD_TO_WISHLIST_CLICKED to adapterPosition)
        },
        itemView.poster_image_view.clicks().subscribe {
          eventsRelay.accept(EVENT_ID_OPEN_DETAILS to movie.id)
        },
        itemView.more_information_check_box.clicks().throttleFirst(500,TimeUnit.MICROSECONDS).subscribe{
          eventsRelay.accept(EVENT_ID_MORE_INFORMATION_CLICKED to adapterPosition)
      }
      )
    }

    private fun expandView() = setDetailsVisibililty(View.VISIBLE)

    private fun collapseView() = setDetailsVisibililty(View.GONE)

    private fun setDetailsVisibililty(visibility: Int) {
      itemView.description_text_view.visibility = visibility
      itemView.votes_text_view.visibility = visibility
    }
  }
}

const val EVENT_ID_ADD_TO_WISHLIST_CLICKED = 0
const val EVENT_ID_OPEN_DETAILS = 1
const val EVENT_ID_MORE_INFORMATION_CLICKED = 2
//TODO: добавить EVENT_ID для свайпов