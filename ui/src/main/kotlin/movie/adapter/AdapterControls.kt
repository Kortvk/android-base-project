package ru.appkode.base.ui.movie.adapter

import android.annotation.SuppressLint
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDoNothing
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToOrigin
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionMoveToSwipedDirection
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem
import kotlinx.android.synthetic.main.item_movie_list.view.*
import movie.adapter.*
import ru.appkode.base.ui.movie.common.UiUtils

interface DragAndDropControls : DraggableItemAdapter<BasicMovieAdapter.MovieVH> {

  fun delegateControlsAdapter(): BasicMovieAdapter

  override fun onGetItemDraggableRange(
    holder: BasicMovieAdapter.MovieVH,
    position: Int
  ): ItemDraggableRange? = null

  override fun onItemDragStarted(position: Int) = delegateControlsAdapter().notifyDataSetChanged()

  override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean = true

  override fun onMoveItem(fromPosition: Int, toPosition: Int) { /* по дороге ничего не делать */
  }

  override fun onItemDragFinished(start: Int, end: Int, result: Boolean) =
    if (result) delegateControlsAdapter().eventsRelay.accept(EVENT_ITEM_DRAGGED_N_DROPPED to (start to end)) else Unit

  override fun onCheckCanStartDrag(
    holder: BasicMovieAdapter.MovieVH,
    position: Int,
    x: Int,
    y: Int
  ): Boolean {
    val offsetX = holder.itemView.layout_controls.left + (holder.itemView.layout_controls.translationX + 0.5f).toInt()
    val offsetY = holder.itemView.layout_controls.top + (holder.itemView.layout_controls.translationY + 0.5f).toInt()
    return UiUtils.hitTest(holder.itemView.drag_handle, x - offsetX, y - offsetY)
  }
}

interface SwipeControls : SwipeableItemAdapter<BasicMovieAdapter.MovieVH> {

  fun delegateControlsAdapter(): BasicMovieAdapter

  override fun onSwipeItemStarted(holder: BasicMovieAdapter.MovieVH, position: Int) =
    delegateControlsAdapter().notifyDataSetChanged()

  @SuppressLint("SwitchIntDef")
  override fun onSwipeItem(
    holder: BasicMovieAdapter.MovieVH,
    position: Int,
    result: Int
  ): SwipeResultAction? =
    when (result) {
      SwipeableItemConstants.RESULT_SWIPED_RIGHT ->
        getSwipeAction { delegateControlsAdapter().eventsRelay.accept(EVENT_ITEM_SWIPED_RIGHT to position) }
      SwipeableItemConstants.RESULT_SWIPED_LEFT ->
        getSwipeAction { delegateControlsAdapter().eventsRelay.accept(EVENT_ITEM_SWIPED_LEFT to position) }
      else -> null
    }

  fun getSwipeAction(action: () -> Unit): SwipeResultAction

  override fun onGetSwipeReactionType(holder: BasicMovieAdapter.MovieVH, position: Int, x: Int, y: Int): Int =
    SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H

  @SuppressLint("SwitchIntDef") //TODO: поменять ресурсы ресурсы (или решить ,что менять не надо)
  override fun onSetSwipeBackground(holder: BasicMovieAdapter.MovieVH, position: Int, type: Int) {
//    val resId = when (type) {
//      SwipeableItemConstants.DRAWABLE_SWIPE_NEUTRAL_BACKGROUND -> R.drawable.item_swiped_bg
//      SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND -> R.drawable.item_swiped_bg
//      SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND -> R.drawable.gradient_bg
//      else -> R.drawable.item_swiped_bg
//    }
//    holder.itemView.setBackgroundResource(resId)
  }
}

object SwipeActions {

  class Remove(private val action: () -> Unit) : SwipeResultActionRemoveItem() {
    override fun onPerformAction() = action.invoke()
  }

  class MoveToOrigin(private val action: () -> Unit) : SwipeResultActionMoveToOrigin() {
    override fun onPerformAction() = action.invoke()
  }

  class MoveToDirection(private val action: () -> Unit) : SwipeResultActionMoveToSwipedDirection() {
    override fun onPerformAction() = action.invoke()
  }

  class DoNothing(private val action: () -> Unit) : SwipeResultActionDoNothing() {
    override fun onPerformAction() = action.invoke()
  }

}