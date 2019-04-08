package movie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_movie_list.view.*
import ru.appkode.base.entities.core.movie.MovieNM
import ru.appkode.base.entities.core.movie.MovieValues
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.filterEvents
import kotlin.properties.Delegates


class MovieNMAdapter : RecyclerView.Adapter<MovieNMAdapter.MovieNMViewHolder>() {

    var items: MutableList<MovieNM> by Delegates.observable(mutableListOf()) { _, _, _ ->
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieNMViewHolder {
        return MovieNMViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie_list, parent, false)
        )
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MovieNMViewHolder, position: Int) {

        with(holder) {
            holder.title.text = items[position].title
            val url = MovieValues.BASE_IMAGE_URL + MovieValues.RECOMENDED_IMAGE_SIZE + items[position].poster_path
            Picasso.get().load(url).into(holder.poster)
            holder.release.text = items[position].release_date
            holder.genre.text = getGenresName(items[position].genres)

            holder.bind(items[position])

        }
    }

    private fun getGenresName(list: ArrayList<MovieNM.Genre>): String {
        var string = ""
        list.forEach { string += it.name + " " }
        return string
    }


    private val eventsRelay: PublishRelay<Pair<Int, Any>> = PublishRelay.create<Pair<Int, Any>>()

    val itemClicked: Observable<Long> = eventsRelay.filterEvents(EVENT_ID_ITEM_CLICKED)
    val itemMoreInformationClcked: Observable<Long> = eventsRelay.filterEvents(EVENT_ID_ITEM_MORE_INFORMATION_CLICKED)
    val eventIdLookedClicked: Observable<Long> = eventsRelay.filterEvents(EVENT_ID_LOOKED_CLICKED)
    val eventIdInWishListClicked: Observable<Long> = eventsRelay.filterEvents(EVENT_ID_INWISH_LIST_CLICKED)

    inner class MovieNMViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val poster: ImageView = view.poster_image_view
        val title: TextView = view.movie_title_text_view
        val release: TextView = view.release_date_text_view
        val genre: TextView = view.movie_genre_text_view
        val moreInformation: CheckBox = view.more_information_check_box
        val looked: CheckBox = view.looked_check_box
        val inWishList: CheckBox = view.in_wish_list

        val postersRecyclerView:RecyclerView=view.movie_posters_recycler_view

        lateinit var movieNM: MovieNM

        init {
            itemView.setOnClickListener {
                eventsRelay.accept(EVENT_ID_ITEM_CLICKED to items[adapterPosition].id)
            }
            moreInformation.setOnClickListener {
                eventsRelay.accept(EVENT_ID_ITEM_MORE_INFORMATION_CLICKED to items[adapterPosition].id)
            }

            looked.setOnClickListener {
                eventsRelay.accept(EVENT_ID_LOOKED_CLICKED to items[adapterPosition].id)
            }
            inWishList.setOnClickListener {
                eventsRelay.accept(EVENT_ID_INWISH_LIST_CLICKED to items[adapterPosition].id)
            }

        }

        fun bind(movieNM: MovieNM) {
            this.movieNM = movieNM
        }
    }

}

private const val EVENT_ID_ITEM_CLICKED = 0
private const val EVENT_ID_ITEM_MORE_INFORMATION_CLICKED = 1
private const val EVENT_ID_LOOKED_CLICKED = 2
private const val EVENT_ID_INWISH_LIST_CLICKED = 3