package ru.appkode.base.ui.task.create

import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.create_task_controller.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.core.core.util.setTextSafe
import ru.appkode.base.ui.task.create.CreateTaskScreen.View
import ru.appkode.base.ui.task.create.CreateTaskScreen.ViewState
import ru.appkode.base.ui.R

class CreateTaskController : BaseMviController<ViewState, View, CreateTaskPresenter>(), View {
  override fun createConfig(): Config {
    return object : Config {
      override val viewLayoutResource = R.layout.create_task_controller
    }
  }

  override fun initializeView(rootView: android.view.View) {
    create_task_toolbar.setNavigationOnClickListener { router.handleBack() }
    create_task_title.doOnTextChanged { text, _, _, _ ->
      eventsRelay.accept(EVENT_ID_TITLE_CHANGED to text.toString())
    }
    create_task_description.doOnTextChanged { text, _, _, _ ->
      eventsRelay.accept(EVENT_ID_DESCRIPTION_CHANGED to text.toString())
    }
  }

  override fun renderViewState(viewState: ViewState) {
    fieldChanged(viewState, { it.task.title }) {
      create_task_toolbar.title = viewState.task.title
      create_task_title.setTextSafe(viewState.task.title)
    }

    fieldChanged(viewState, { it.task.description }) {
      create_task_description.setTextSafe(viewState.task.description)
    }

    fieldChanged(viewState, { it.isButtonEnabled }) {
      create_task_button_create.isEnabled = viewState.isButtonEnabled
    }

    fieldChanged(viewState, { it.state }) {
      renderLceState(viewState.state)
    }
  }

  private fun renderLceState(state: LceState<Unit>) {
    create_task_loading.isVisible = state.isLoading
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

  override fun createTaskIntent(): Observable<Unit> {
    return create_task_button_create.clicks()
  }

  override fun createPresenter(): CreateTaskPresenter {
    return CreateTaskPresenter(
      DefaultAppSchedulers,
      RepositoryHelper.getTaskRepository(DefaultAppSchedulers),
      this.router!!
    )
  }
}

private const val EVENT_ID_TITLE_CHANGED = 0
private const val EVENT_ID_DESCRIPTION_CHANGED = 1
