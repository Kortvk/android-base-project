package ru.appkode.base.ui.movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_movie_posters.view.*
import ru.appkode.base.entities.core.movie.BASE_IMAGE_URL
import ru.appkode.base.entities.core.movie.MoviePosterNM
import ru.appkode.base.entities.core.movie.RECOMENDED_IMAGE_SIZE
import ru.appkode.base.ui.R


class MoviePosterAdapter: RecyclerView.Adapter<MoviePosterAdapter.MoviePosterHolder>() {

    var data: List< MoviePosterNM.Poster> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviePosterHolder =MoviePosterHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie_posters, parent, false)
            )

    override fun getItemCount(): Int= data.size

    override fun onBindViewHolder(holder: MoviePosterHolder, position: Int) {
        with(holder){
            val url = BASE_IMAGE_URL + RECOMENDED_IMAGE_SIZE +  data[position].file_path
            Picasso.get().load(url).error(R.drawable.ic_close_black_24dp).into(holder.poster)
        }
    }
    inner class MoviePosterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val poster: ImageView = view.movie_poster_image_view
    }
}