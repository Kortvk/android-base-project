package ru.appkode.base.ui.task.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.task_list_item.view.*
import ru.appkode.base.entities.core.task.TaskUM
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.R

class TaskListAdapter : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {

  var data: List<TaskUM> = emptyList()
    set(value) {
      DiffUtil.calculateDiff(object : DiffUtil.Callback() {
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          return field[oldItemPosition].id == value[newItemPosition].id
        }

        override fun getOldListSize() = field.size

        override fun getNewListSize() = value.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
          return field[oldItemPosition] == value[newItemPosition]
        }

      }).dispatchUpdatesTo(this)

      field = value
    }

  private val eventsRelay: PublishRelay<Pair<Int, Any>> = PublishRelay.create<Pair<Int, Any>>()

  val itemClicked: Observable<Long> = eventsRelay.filterEvents(EVENT_ID_ITEM_CLICKED)
  val itemSwitched: Observable<Long> = eventsRelay.filterEvents(EVENT_ID_ITEM_SWITCHED)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false))
  }

  override fun getItemCount() = data.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    with(holder) {
      checkBox.isChecked = data[position].isChecked
      title.text = data[position].title
      description.text = data[position].description
    }
  }

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val checkBox: CheckBox = view.task_list_item_check
    val title: TextView = view.task_list_item_title
    val description: TextView = view.task_list_item_description

    init {
      checkBox.setOnClickListener {
        eventsRelay.accept(EVENT_ID_ITEM_SWITCHED to data[adapterPosition].id)
      }
      view.setOnClickListener {
        eventsRelay.accept(EVENT_ID_ITEM_CLICKED to data[adapterPosition].id)
      }
    }
  }
}

internal const val EVENT_ID_ITEM_SWITCHED = 0
internal const val EVENT_ID_ITEM_CLICKED = 1
