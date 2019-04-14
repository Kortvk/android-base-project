package movie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_cast.view.*
import ru.appkode.base.entities.core.movie.BASE_IMAGE_URL
import ru.appkode.base.entities.core.movie.CastUM
import ru.appkode.base.entities.core.movie.IMAGE_PROFILE_SIZE
import ru.appkode.base.entities.core.movie.getProfilePath
import ru.appkode.base.ui.R
import kotlin.properties.Delegates

class CastAdapter : RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

  var items: List<CastUM> by Delegates.observable(emptyList()) { _, _, _ ->
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder {
    return CastViewHolder(
      LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
    )
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: CastViewHolder, position: Int) =
    holder.bind(items[position])

  inner class CastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    fun bind(cast: CastUM) {
      itemView.tv_cast_name.text = cast.name
      if (cast.profilePath != null) {
        Glide.with(itemView).load(cast.getProfilePath())
          .into(itemView.iv_cast).onLoadFailed(itemView.context.getDrawable(R.drawable.no_photo))
      }
      itemView.tv_cast_role.text = cast.character
    }
  }
}
