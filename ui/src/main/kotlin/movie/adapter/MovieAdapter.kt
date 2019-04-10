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
import ru.appkode.base.entities.core.movie.BASE_IMAGE_URL
import ru.appkode.base.entities.core.movie.IMAGE_PROFILE_SIZE
import ru.appkode.base.entities.core.movie.MovieBriefUM
import ru.appkode.base.ui.R
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieNMViewHolder>() {

  var items: List<MovieBriefUM> by Delegates.observable(emptyList()) { _, _, _ ->
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
      itemView.movie_title_text_view.text = movie.title
      itemView.in_wish_list.isChecked = movie.isInWishList
      val url = BASE_IMAGE_URL + IMAGE_PROFILE_SIZE + movie.poster
      Picasso.get().load(url).into(itemView.poster_image_view)
      itemView.release_date_text_view.text = movie.releaseDate
      itemView.movie_genre_text_view.text = movie.genres.joinToString(separator = ", ")
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
        }
      )
      //TODO: забиндить свайпы тут
    }
  }
}

const val EVENT_ID_ADD_TO_WISHLIST_CLICKED = 0
const val EVENT_ID_OPEN_DETAILS = 1
//TODO: добавить EVENT_ID для свайпов