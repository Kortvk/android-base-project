package ru.appkode.base.ui.movie.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_cast.view.iv_cast
import kotlinx.android.synthetic.main.item_cast.view.tv_cast_name
import ru.appkode.base.entities.core.movie.BASE_IMAGE_URL
import ru.appkode.base.entities.core.movie.CastUM
import ru.appkode.base.entities.core.movie.IMAGE_SIZE_SMALL
import ru.appkode.base.ui.R
import kotlin.properties.Delegates


class CastAdapter : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

  var items: List<CastUM> by Delegates.observable(emptyList()) { _, _, _ ->
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
    return CastViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_movie_list, parent, false)
    )
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: CastViewHolder, position: Int) = holder.bind(items[position])

  inner class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(cast: CastUM) {
      itemView.tv_cast_name.text = cast.name
      Glide.with(itemView).load(BASE_IMAGE_URL + IMAGE_SIZE_SMALL + cast.profilePath)
        .into(itemView.iv_cast)
    }
  }
}
