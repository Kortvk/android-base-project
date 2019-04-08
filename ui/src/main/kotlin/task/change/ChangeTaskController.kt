package ru.appkode.base.ui.task.change

import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.change_task_controller.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.core.core.util.setTextSafe
import ru.appkode.base.ui.task.change.ChangeTaskScreen.View
import ru.appkode.base.ui.task.change.ChangeTaskScreen.ViewState
import ru.appkode.base.ui.R

class ChangeTaskController : BaseMviController<ViewState, View, ChangeTaskPresenter>(), View {

  companion object {
    fun createController(taskId: Long): ChangeTaskController {
      return ChangeTaskController().apply {
        args.putLong(ARG_TASK_ID_KEY, taskId)
      }
    }
  }

  override fun createConfig(): Config {
    return object : Config {
      override val viewLayoutResource = R.layout.change_task_controller
    }
  }

  private val taskId: Long by lazy { args.getLong(ARG_TASK_ID_KEY) }

  override fun initializeView(rootView: android.view.View) {
    change_task_toolbar.setNavigationOnClickListener { router.handleBack() }
    change_task_title.doOnTextChanged { text, _, _, _ ->
      eventsRelay.accept(EVENT_ID_TITLE_CHANGED to text.toString())
    }
    change_task_description.doOnTextChanged { text, _, _, _ ->
      eventsRelay.accept(EVENT_ID_DESCRIPTION_CHANGED to text.toString())
    }
  }

  override fun renderViewState(viewState: ViewState) {
    fieldChanged(viewState, { it.task.title }) {
      change_task_toolbar.title = viewState.task.title
      change_task_title.setTextSafe(viewState.task.title)
    }

    fieldChanged(viewState, { it.task.description }) {
      change_task_description.setTextSafe(viewState.task.description)
    }

    fieldChanged(viewState, { it.isButtonEnabled }) {
      change_task_button_save.isEnabled = viewState.isButtonEnabled
    }

    fieldChanged(viewState, { it.state }) {
      renderLceState(viewState.state)
    }
  }

  private fun renderLceState(state: LceState<Unit>) {
    change_task_loading.isVisible = state.isLoading
    if (state.isError) {
      Snackbar
        .make(
          this.containerView!!,
          state.asError(),
          Snackbar.LENGTH_LONG
        )
        .show()
    }
  }

  override fun changeTaskTitleIntent(): Observable<String> {
    return eventsRelay.filterEvents(EVENT_ID_TITLE_CHANGED)
  }

  override fun changeTaskDescription(): Observable<String> {
    return eventsRelay.filterEvents(EVENT_ID_DESCRIPTION_CHANGED)
  }

  override fun saveTaskIntent(): Observable<Unit> {
    return change_task_button_save.clicks()
  }

  override fun deleteTask(): Observable<Unit> {
    return change_task_remove.clicks()
  }

  override fun createPresenter(): ChangeTaskPresenter {
    return ChangeTaskPresenter(
      DefaultAppSchedulers,
      RepositoryHelper.getTaskRepository(DefaultAppSchedulers),
      this.router!!,
      taskId
    )
  }
}

private const val EVENT_ID_TITLE_CHANGED = 0
private const val EVENT_ID_DESCRIPTION_CHANGED = 1
private const val ARG_TASK_ID_KEY = "task_id"
