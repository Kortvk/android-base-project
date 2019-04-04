package movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_list_item.view.*
import ru.appkode.base.entities.core.movie.MovieNM
import ru.appkode.base.entities.core.movie.MovieValues
import ru.appkode.base.ui.R


class MovieNMAdapter(var listener: ViewHolderClickListener) :
    RecyclerView.Adapter<MovieNMAdapter.MovieNMViewHolder>() {


    var data: List<MovieNM> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieNMViewHolder {
        return MovieNMViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false),
            listener
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: MovieNMViewHolder, position: Int) {

        with(holder) {
            holder.title.text = data[position].title
            val url = MovieValues.BASE_IMAGE_URL + MovieValues.RECOMENDED_IMAGE_SIZE + data[position].poster_path
            Picasso.get().load(url).into(holder.poster)
            holder.release.text = data[position].release_date
            holder.genre.text = getGenresName(data[position].genres)

            holder.bind(data[position])
            holder.moreInformationButton.setOnClickListener {
                TODO() // ?????
            }
        }
    }

    private fun getGenresName(list: ArrayList<MovieNM.Genre>): String {
        var string = ""
        list.forEach { string += it.name + " " }
        return string
    }

    interface ViewHolderClickListener {
        fun onViewHolderClick(item: MovieNM)
    }

    inner class MovieNMViewHolder(view: View, listener: ViewHolderClickListener) : RecyclerView.ViewHolder(view) {

        val poster: ImageView = view.poster_image_view
        val title: TextView = view.movie_title_text_view
        val release: TextView = view.release_date_text_view
        val genre: TextView = view.movie_genre_text_view
        val moreInformationButton: Button = view.more_information_button
        val looked: CheckBox = view.looked_check_box
        val inWishList: CheckBox = view.in_wish_list
        lateinit var movieNM: MovieNM

        init {
            itemView.setOnClickListener {
                listener.onViewHolderClick(movieNM)
            }
        }

        fun bind(movieNM: MovieNM) {
            this.movieNM = movieNM
        }
    }
}