package movie.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractSwipeableItemViewHolder
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
import ru.appkode.base.ui.movie.common.UiUtils
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class MovieAdapter : RecyclerView.Adapter<MovieAdapter.MovieNMViewHolder>(),
  DraggableItemAdapter<MovieAdapter.MovieNMViewHolder>,
  SwipeableItemAdapter<MovieAdapter.MovieNMViewHolder>
{

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

  override fun onViewRecycled(holder: MovieNMViewHolder) = holder.unbind()

  /** Имплементация методов интерфейса DraggableItemAdapter */
  override fun onGetItemDraggableRange(holder: MovieNMViewHolder, position: Int): ItemDraggableRange? = null

  override fun onItemDragStarted(position: Int) = notifyDataSetChanged()

  override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean = true

  override fun onMoveItem(fromPosition: Int, toPosition: Int) { /* по дороге ничего не делать */ }

  override fun onItemDragFinished(start: Int, end: Int, result: Boolean) =
    if (result) eventsRelay.accept(EVENT_ITEM_DRAGED_N_DROPPED to (start to end)) else Unit

  override fun onCheckCanStartDrag(holder: MovieNMViewHolder, position: Int, x: Int, y: Int): Boolean {
    val offsetX = (holder.itemView.left + (holder.itemView.translationX + 0.5f)).toInt()
    val offsetY = (holder.itemView.top + (holder.itemView.translationY + 0.5f)).toInt()
    return UiUtils.hitTest(holder.itemView.check_box_in_history, x - offsetX, y - offsetY)
  }

  /** Имплементация методов интерфейса SwipeableItemAdapter */
  override fun onSwipeItemStarted(holder: MovieNMViewHolder, position: Int) = notifyDataSetChanged()
  @SuppressLint("SwitchIntDef")
  override fun onSwipeItem(holder: MovieNMViewHolder, position: Int, result: Int): SwipeResultAction? =
    when(result) {
      SwipeableItemConstants.RESULT_SWIPED_RIGHT ->
        SwipeRight {
          eventsRelay.accept(EVENT_ITEM_SWIPED_RIGHT to position) }
      SwipeableItemConstants.RESULT_SWIPED_LEFT ->
        SwipeLeft {
          eventsRelay.accept(EVENT_ITEM_SWIPED_LEFT to position) }
      else -> null
    }
  override fun onGetSwipeReactionType(holder: MovieNMViewHolder, position: Int, x: Int, y: Int): Int =
    SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H

  @SuppressLint("SwitchIntDef") //TODO: поменять ресурсы ресурсы (или решить ,что менять не надо)
  override fun onSetSwipeBackground(holder: MovieNMViewHolder, position: Int, type: Int) {
    val resId = when (type) {
      SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND -> R.drawable.gradient_bg
      SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND -> R.drawable.gradient_bg
      SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND -> R.drawable.gradient_bg
      else -> R.drawable.gradient_bg
    }
    holder.itemView.setBackgroundResource(resId)
  }

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
        itemView.more_information_check_box.clicks().throttleFirst(500, TimeUnit.MICROSECONDS).subscribe {
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

private class SwipeRight(private val action: () -> Unit)
  : SwipeResultActionRemoveItem() {
  override fun onPerformAction() = action.invoke()
}

private class SwipeLeft(private val action: () -> Unit)
  : SwipeResultActionRemoveItem() {
  override fun onPerformAction() = action.invoke()
}

const val EVENT_ID_ADD_TO_WISHLIST_CLICKED = 0
const val EVENT_ID_OPEN_DETAILS = 1
const val EVENT_ID_MORE_INFORMATION_CLICKED = 2
const val EVENT_ITEM_DRAGED_N_DROPPED = 3
const val EVENT_ITEM_SWIPED_RIGHT = 4
const val EVENT_ITEM_SWIPED_LEFT = 5
